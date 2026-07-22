#!/usr/bin/env python3
"""
Transform AI-exported literature chunks from docs/data/data*.json into
LexiLearn BE rows.

Default mode is dry-run. It parses data, groups real reader sections by
metadata.section_slug, merges overlapping chunks, and prints an import report.
It uses the new metadata contract only; old fields such as ten_tac_pham,
tac_gia, the_loai, lop, and hoc_ki are intentionally ignored.

Apply mode needs a PostgreSQL driver:
  - psycopg2, or
  - psycopg v3

Examples:
  python3 scripts/import_literature_data.py --dry-run
  python3 scripts/import_literature_data.py --dry-run --preview
  python3 scripts/import_literature_data.py --apply --replace
  python3 scripts/import_literature_data.py --apply --replace --include-commentaries
"""

from __future__ import annotations

import argparse
import glob
import json
import os
import re
import sys
from collections import defaultdict
from dataclasses import dataclass, field
from pathlib import Path
from typing import Any


ROOT = Path(__file__).resolve().parents[1]
DEFAULT_DATA_GLOB = "docs/data/data*.json"
SCHEMA_VERSION = "literature_seed.v1"

PERIOD_VALUES = {"dan_gian", "trung_dai", "hien_dai"}
GENRE_VALUES = {"Truyện ngắn", "Tiểu thuyết", "Thơ ca", "Kịch", "Ký", "Truyện dân gian"}
SUB_GENRE_VALUES = {
    "Truyện ngắn hiện thực",
    "Truyện ngắn lãng mạn",
    "Truyện ngắn trào phúng",
    "Truyện ngắn tâm lý",
    "Tiểu thuyết hiện thực",
    "Tiểu thuyết lịch sử",
    "Tiểu thuyết tâm lý",
    "Tiểu thuyết chiến tranh",
    "Thơ tự do",
    "Thơ Đường luật",
    "Thơ lục bát",
    "Thơ thất ngôn bát cú",
    "Bút ký",
    "Tùy bút",
    "Phóng sự",
    "Hồi ký",
    "Kịch nói",
    "Bi kịch",
    "Hài kịch",
    "Truyện cổ tích",
    "Sử thi",
    "Truyện thơ dân gian",
    "Ca dao",
    "Song thất lục bát",
    "Truyền kỳ",
    "Văn chính luận",
    "Văn tế",
}
FOLK_SUB_GENRE_VALUES = {"Truyện cổ tích", "Sử thi", "Truyện thơ dân gian"}
LEGACY_GENRE_MAP = {
    "tho_ca": "Thơ ca",
    "truyen_tho": "Thơ ca",
    "truyen_ngan": "Truyện ngắn",
    "truyen_dan_gian": "Truyện dân gian",
    "su_thi": "Truyện dân gian",
    "khao_cuu": "Ký",
    "van_chinh_luan": "Ký",
    "tieu_thuyet": "Tiểu thuyết",
    "ky": "Ký",
    "kich": "Kịch",
}
LEGACY_SUB_GENRE_MAP = {
    "truyen_ngan_hien_thuc": "Truyện ngắn hiện thực",
    "truyen_ngan_lang_man": "Truyện ngắn lãng mạn",
    "truyen_ngan_trao_phung": "Truyện ngắn trào phúng",
    "truyen_ngan_tam_ly": "Truyện ngắn tâm lý",
    "tieu_thuyet_hien_thuc": "Tiểu thuyết hiện thực",
    "tieu_thuyet_lich_su": "Tiểu thuyết lịch sử",
    "tieu_thuyet_tam_ly": "Tiểu thuyết tâm lý",
    "tieu_thuyet_chien_tranh": "Tiểu thuyết chiến tranh",
    "tho_tu_do": "Thơ tự do",
    "tho_duong_luat": "Thơ Đường luật",
    "tho_luc_bat": "Thơ lục bát",
    "luc_bat": "Thơ lục bát",
    "that_ngon_bat_cu": "Thơ thất ngôn bát cú",
    "that_ngon_tu_tuyet": "Thơ Đường luật",
    "tho_nom_duong_luat": "Thơ Đường luật",
    "but_ky": "Bút ký",
    "tuy_but": "Tùy bút",
    "phong_su": "Phóng sự",
    "hoi_ky": "Hồi ký",
    "hoi_ki": "Hồi ký",
    "kich_noi": "Kịch nói",
    "bi_kich": "Bi kịch",
    "hai_kich": "Hài kịch",
    "truyen_co_tich": "Truyện cổ tích",
    "su_thi": "Sử thi",
    "su_thi_dan_gian": "Sử thi",
    "truyen_tho_dan_gian": "Truyện thơ dân gian",
    "truyen_tho_nom": "Thơ lục bát",
    "ca_dao": "Ca dao",
    "song_that_luc_bat": "Song thất lục bát",
    "truyen_ky": "Truyền kỳ",
    "truyen_truyen_ky": "Truyền kỳ",
    "van_chinh_luan": "Văn chính luận",
    "van_te": "Văn tế",
}
CONTENT_TYPE_VALUES = {"PROSE", "POETRY", "MIXED"}
KNOWN_CATEGORIES = {
    "text_section",
    "author_bio",
    "historical_context",
    "title_meaning",
    "layout_analysis",
    "reality_value",
    "human_value",
    "art_value",
}
SLUG_PATTERN = re.compile(r"^[a-z0-9][a-z0-9_-]*$")


@dataclass
class Chunk:
    row: dict[str, Any]
    metadata: dict[str, Any]
    position: dict[str, Any]
    source_file: str
    source_row_number: int

    @property
    def schema_version(self) -> str:
        return str(self.metadata.get("schema_version") or "").strip()

    @property
    def work_title(self) -> str:
        return str(self.metadata.get("work_title") or "").strip()

    @property
    def work_slug(self) -> str:
        return str(self.metadata.get("work_slug") or "").strip()

    @property
    def author_name(self) -> str:
        return str(self.metadata.get("author_name") or "").strip()

    @property
    def author_slug(self) -> str:
        return str(self.metadata.get("author_slug") or "").strip()

    @property
    def category(self) -> str:
        return str(self.metadata.get("chunk_category") or "").strip()

    @property
    def section_slug(self) -> str | None:
        value = self.metadata.get("section_slug")
        return str(value).strip() if value else None

    @property
    def section_title(self) -> str | None:
        value = self.metadata.get("section_title")
        return str(value).strip() if value else None

    @property
    def content(self) -> str:
        return str(self.row.get("content") or "").strip()

    @property
    def content_type_raw(self) -> str:
        return str(self.metadata.get("content_type") or self.row.get("content_type") or "").strip()

    @property
    def chunk_index(self) -> int:
        value = self.position.get("chunk_index")
        return int(value) if isinstance(value, (int, float)) or str(value).isdigit() else 0

    @property
    def has_overlap(self) -> bool:
        return bool(self.row.get("has_overlap"))

    @property
    def section_order(self) -> int | None:
        return to_int_or_none(self.metadata.get("section_order"))


@dataclass
class Section:
    number: int
    section_key: str
    title: str
    content_type: str
    content: str
    chunk_count: int
    overlap_count: int
    merge_warning_count: int

    @property
    def word_count(self) -> int:
        return count_words(self.content)


@dataclass
class WorkSeed:
    raw_title: str
    title: str
    slug: str
    author_name: str
    author_slug: str
    author_period: str
    author_bio: str | None
    genre: str
    sub_genre: str | None
    period: str
    grade: int | None
    semester: int | None
    publish_year: int | None
    summary: str | None
    historical_context: str | None
    realistic_value: str | None
    humanistic_value: str | None
    artistic_value: str | None
    title_meaning: str | None
    layout_analysis: list[str]
    extra_commentary_content: dict[str, list[str]]
    sections: list[Section] = field(default_factory=list)
    source_files: set[str] = field(default_factory=set)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Import AI literature export JSON into LexiLearn PostgreSQL."
    )
    parser.add_argument(
        "--data-glob",
        default=DEFAULT_DATA_GLOB,
        help=f"Glob for export JSON files, default: {DEFAULT_DATA_GLOB}",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Validate and print report only. This is the default unless --apply is set.",
    )
    parser.add_argument(
        "--apply",
        action="store_true",
        help="Write to PostgreSQL. Requires psycopg2 or psycopg.",
    )
    parser.add_argument(
        "--replace",
        action="store_true",
        help="Before inserting sections, delete existing sections for imported works.",
    )
    parser.add_argument(
        "--include-empty-works",
        action="store_true",
        help="Also upsert works that have no readingSections. Default skips them in --apply.",
    )
    parser.add_argument(
        "--include-commentaries",
        action="store_true",
        help="Insert simple editorial commentaries generated from title/layout/values.",
    )
    parser.add_argument(
        "--preview",
        action="store_true",
        help="Print short previews of generated sections.",
    )
    parser.add_argument(
        "--min-overlap",
        type=int,
        default=24,
        help="Minimum exact suffix/prefix overlap length used during chunk merge.",
    )
    parser.add_argument(
        "--env-file",
        default=".env",
        help="Optional .env file to load DB_* variables from. Default: .env",
    )
    parser.add_argument("--db-host", default=None)
    parser.add_argument("--db-port", type=int, default=None)
    parser.add_argument("--db-name", default=None)
    parser.add_argument("--db-user", default=None)
    parser.add_argument("--db-password", default=None)
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    if not args.apply:
        args.dry_run = True

    load_env_file(ROOT / args.env_file)
    files = sorted(glob.glob(str(ROOT / args.data_glob)))
    files = [f for f in files if not f.endswith("package-lock.json")]

    if not files:
        print(f"No files matched {args.data_glob}", file=sys.stderr)
        return 2

    chunks = load_chunks(files)
    validation_errors = validate_chunks(chunks)
    if validation_errors:
        print_validation_errors(validation_errors)
        return 2

    seeds = build_work_seeds(chunks, min_overlap=args.min_overlap)
    print_report(seeds, chunks, files, preview=args.preview)

    if not args.apply:
        print("\nDRY-RUN only. No DB changes were made.")
        return 0

    importable = [seed for seed in seeds if seed.sections or args.include_empty_works]
    skipped = [seed for seed in seeds if not seed.sections and not args.include_empty_works]
    if skipped:
        print(
            "\nSkipping works without readingSections in apply mode: "
            + ", ".join(seed.title for seed in skipped)
        )

    apply_to_db(
        importable,
        args=args,
    )
    return 0


def load_env_file(path: Path) -> None:
    if not path.exists():
        return
    for line in path.read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        key = key.strip()
        value = value.strip().strip("'").strip('"')
        os.environ.setdefault(key, value)


def load_json_array(path: str) -> list[dict[str, Any]]:
    # utf-8-sig strips BOM if Mongo/export tool wrote one.
    with open(path, "r", encoding="utf-8-sig") as f:
        data = json.load(f)
    if not isinstance(data, list):
        raise ValueError(f"{path} must contain a JSON array")
    return data


def load_chunks(files: list[str]) -> list[Chunk]:
    chunks: list[Chunk] = []
    seen: set[tuple[str, str, int]] = set()
    for path in files:
        rows = load_json_array(path)
        for source_row_number, row in enumerate(rows, start=1):
            metadata = parse_nested_json(row.get("metadata"), field_name="metadata", path=path)
            position = parse_nested_json(row.get("position"), field_name="position", path=path)
            chunk = Chunk(
                row=row,
                metadata=metadata,
                position=position,
                source_file=os.path.relpath(path, ROOT),
                source_row_number=source_row_number,
            )
            key = (chunk.work_slug, str(row.get("chunk_id") or ""), chunk.chunk_index)
            if key in seen:
                continue
            seen.add(key)
            chunks.append(chunk)
    return chunks


def parse_nested_json(value: Any, *, field_name: str, path: str) -> dict[str, Any]:
    if value is None:
        return {}
    if isinstance(value, dict):
        return value
    if isinstance(value, str):
        try:
            parsed = json.loads(value)
        except json.JSONDecodeError as exc:
            raise ValueError(f"Cannot parse {field_name} JSON string in {path}: {exc}") from exc
        if isinstance(parsed, dict):
            return parsed
    raise ValueError(f"{field_name} in {path} must be an object or JSON object string")


def validate_chunks(chunks: list[Chunk]) -> list[str]:
    errors: list[str] = []
    required_metadata = [
        "schema_version",
        "work_title",
        "work_slug",
        "author_name",
        "author_slug",
        "author_period",
        "work_period",
        "genre",
        "grade",
        "semester",
        "chunk_category",
    ]
    required_text_section = [
        "section_slug",
        "section_title",
        "section_order",
        "content_type",
    ]

    work_identity: dict[str, dict[str, Any]] = {}
    section_identity: dict[tuple[str, str], dict[str, Any]] = {}
    section_order_by_work: dict[tuple[str, int], str] = {}
    section_slugs_by_work = collect_text_section_slugs_by_work(chunks)

    for chunk in chunks:
        label = f"{chunk.source_file} row#{chunk.source_row_number}"
        metadata = chunk.metadata

        for field_name in required_metadata:
            if is_blank(metadata.get(field_name)):
                errors.append(f"{label}: missing metadata.{field_name}")

        if errors and is_blank(chunk.work_slug):
            continue

        if chunk.schema_version and chunk.schema_version != SCHEMA_VERSION:
            errors.append(
                f"{label}: metadata.schema_version must be {SCHEMA_VERSION}, got {chunk.schema_version!r}"
            )

        validate_slug_like(errors, label, "work_slug", chunk.work_slug)
        validate_slug_like(errors, label, "author_slug", chunk.author_slug)
        sub_genre = normalize_sub_genre(metadata.get("sub_genre"))
        genre = normalize_genre(metadata.get("genre"), sub_genre=sub_genre)
        if genre not in GENRE_VALUES:
            errors.append(
                f"{label}: metadata.genre must be one of {sorted(GENRE_VALUES)}, "
                f"got {metadata.get('genre')!r}"
            )
        if sub_genre and sub_genre not in SUB_GENRE_VALUES:
            errors.append(
                f"{label}: metadata.sub_genre must be one of {sorted(SUB_GENRE_VALUES)}, "
                f"got {metadata.get('sub_genre')!r}"
            )

        if metadata.get("author_period") not in PERIOD_VALUES and not is_blank(metadata.get("author_period")):
            errors.append(
                f"{label}: metadata.author_period must be one of {sorted(PERIOD_VALUES)}, "
                f"got {metadata.get('author_period')!r}"
            )
        if metadata.get("work_period") not in PERIOD_VALUES and not is_blank(metadata.get("work_period")):
            errors.append(
                f"{label}: metadata.work_period must be one of {sorted(PERIOD_VALUES)}, "
                f"got {metadata.get('work_period')!r}"
            )

        grade = to_int_or_none(metadata.get("grade"))
        semester = to_int_or_none(metadata.get("semester"))
        if grade not in {10, 11, 12}:
            errors.append(f"{label}: metadata.grade must be 10, 11, or 12, got {metadata.get('grade')!r}")
        if semester not in {1, 2}:
            errors.append(f"{label}: metadata.semester must be 1 or 2, got {metadata.get('semester')!r}")

        work_key = chunk.work_slug
        if work_key:
            current_identity = {
                "work_title": metadata.get("work_title"),
                "author_name": metadata.get("author_name"),
                "author_slug": metadata.get("author_slug"),
                "author_period": metadata.get("author_period"),
                "work_period": metadata.get("work_period"),
                "genre": genre,
                "sub_genre": sub_genre,
                "grade": grade,
                "semester": semester,
                "publish_year": to_int_or_none(metadata.get("publish_year")),
            }
            previous_identity = work_identity.setdefault(work_key, current_identity)
            if previous_identity != current_identity:
                errors.append(
                    f"{label}: inconsistent work metadata for work_slug={work_key!r}; "
                    "all chunks of one work must share title/author/period/genre/grade/semester"
                )

        if chunk.category == "text_section":
            for field_name in required_text_section:
                if field_name == "section_order" and can_default_section_order(chunk, section_slugs_by_work):
                    continue
                if is_blank(metadata.get(field_name)):
                    errors.append(f"{label}: missing metadata.{field_name} for text_section")

            section_order = resolve_section_order(chunk, section_slugs_by_work)
            if section_order is None or section_order <= 0:
                errors.append(
                    f"{label}: metadata.section_order must be a positive integer, "
                    f"got {metadata.get('section_order')!r}"
                )

            content_type = normalize_content_type(chunk.content_type_raw)
            if content_type not in CONTENT_TYPE_VALUES:
                errors.append(
                    f"{label}: metadata.content_type must be one of {sorted(CONTENT_TYPE_VALUES)}, "
                    f"got {chunk.content_type_raw!r}"
                )

            if chunk.section_slug:
                validate_slug_like(errors, label, "section_slug", chunk.section_slug)

            if work_key and chunk.section_slug:
                current_section = {
                    "section_title": chunk.section_title,
                    "section_order": section_order,
                    "content_type": content_type,
                }
                section_key = (work_key, chunk.section_slug)
                previous_section = section_identity.setdefault(section_key, current_section)
                if previous_section != current_section:
                    errors.append(
                        f"{label}: inconsistent section metadata for "
                        f"work_slug={work_key!r}, section_slug={chunk.section_slug!r}"
                    )

                if section_order is not None:
                    order_key = (work_key, section_order)
                    previous_slug = section_order_by_work.setdefault(order_key, chunk.section_slug)
                    if previous_slug != chunk.section_slug:
                        errors.append(
                            f"{label}: duplicate metadata.section_order={section_order} in "
                            f"work_slug={work_key!r}; already used by {previous_slug!r}, "
                            f"also used by {chunk.section_slug!r}"
                        )
        elif chunk.category and chunk.category not in KNOWN_CATEGORIES:
            # Allowed: unknown categories can become optional commentaries.
            pass
        elif not chunk.category:
            errors.append(f"{label}: missing metadata.chunk_category")

    return errors


def collect_text_section_slugs_by_work(chunks: list[Chunk]) -> dict[str, set[str]]:
    result: dict[str, set[str]] = defaultdict(set)
    for chunk in chunks:
        if chunk.category == "text_section" and chunk.work_slug and chunk.section_slug:
            result[chunk.work_slug].add(chunk.section_slug)
    return result


def can_default_section_order(chunk: Chunk, section_slugs_by_work: dict[str, set[str]]) -> bool:
    return (
        chunk.category == "text_section"
        and chunk.section_order is None
        and chunk.work_slug in section_slugs_by_work
        and len(section_slugs_by_work[chunk.work_slug]) == 1
    )


def resolve_section_order(chunk: Chunk, section_slugs_by_work: dict[str, set[str]]) -> int | None:
    if can_default_section_order(chunk, section_slugs_by_work):
        return 1
    return chunk.section_order


def print_validation_errors(errors: list[str]) -> None:
    print("Literature import validation failed")
    print("=" * 35)
    print(f"Errors: {len(errors)}")
    for error in errors[:80]:
        print(f"- {error}")
    if len(errors) > 80:
        print(f"... and {len(errors) - 80} more")
    print("\nNo DB changes were made.")


def validate_slug_like(errors: list[str], label: str, field_name: str, value: Any) -> None:
    if is_blank(value):
        return
    text = str(value).strip()
    if not SLUG_PATTERN.match(text):
        errors.append(
            f"{label}: metadata.{field_name} must be slug/snake_case "
            f"(lowercase a-z, 0-9, '_' or '-'), got {text!r}"
        )


def normalize_sub_genre(value: Any) -> str | None:
    if is_blank(value):
        return None
    text = str(value or "").strip()
    return LEGACY_SUB_GENRE_MAP.get(text, text)


def normalize_genre(value: Any, *, sub_genre: str | None = None) -> str:
    text = str(value or "").strip()
    if sub_genre in FOLK_SUB_GENRE_VALUES:
        return "Truyện dân gian"
    return LEGACY_GENRE_MAP.get(text, text)


def is_blank(value: Any) -> bool:
    return value is None or str(value).strip() == ""


def build_work_seeds(chunks: list[Chunk], *, min_overlap: int) -> list[WorkSeed]:
    by_work: dict[str, list[Chunk]] = defaultdict(list)
    for chunk in chunks:
        by_work[chunk.work_slug].append(chunk)

    seeds = []
    for work_slug, work_chunks in sorted(
        by_work.items(), key=lambda item: min(c.chunk_index for c in item[1])
    ):
        work_chunks = sorted(work_chunks, key=lambda c: c.chunk_index)
        first_meta = work_chunks[0].metadata
        title = str(first_meta["work_title"]).strip()
        author_name = str(first_meta["author_name"]).strip()
        author_slug = str(first_meta["author_slug"]).strip()
        author_period = str(first_meta["author_period"]).strip()
        sub_genre = normalize_sub_genre(first_meta.get("sub_genre"))
        genre = normalize_genre(first_meta["genre"], sub_genre=sub_genre)
        work_period = str(first_meta["work_period"]).strip()

        category_content = collect_category_content(work_chunks)
        sections = build_sections(work_chunks, min_overlap=min_overlap)

        seed = WorkSeed(
            raw_title=title,
            title=title,
            slug=work_slug,
            author_name=author_name,
            author_slug=author_slug,
            author_period=author_period,
            author_bio=first_or_none(category_content["author_bio"]),
            genre=genre,
            sub_genre=sub_genre,
            period=work_period,
            grade=to_int_or_none(first_meta.get("grade")),
            semester=to_int_or_none(first_meta.get("semester")),
            publish_year=to_int_or_none(first_meta.get("publish_year")),
            summary=build_summary(title, first_or_none(category_content["title_meaning"])),
            historical_context=first_or_none(category_content["historical_context"]),
            realistic_value=first_or_none(category_content["reality_value"]),
            humanistic_value=first_or_none(category_content["human_value"]),
            artistic_value=first_or_none(category_content["art_value"]),
            title_meaning=first_or_none(category_content["title_meaning"]),
            layout_analysis=category_content["layout_analysis"],
            extra_commentary_content={
                category: values
                for category, values in category_content.items()
                if category not in KNOWN_CATEGORIES
            },
            sections=sections,
            source_files={chunk.source_file for chunk in work_chunks},
        )
        seeds.append(seed)

    return seeds


def collect_category_content(chunks: list[Chunk]) -> dict[str, list[str]]:
    result: dict[str, list[str]] = defaultdict(list)
    for chunk in sorted(chunks, key=lambda c: c.chunk_index):
        if not chunk.content:
            continue
        result[chunk.category].append(chunk.content)
    return result


def build_sections(chunks: list[Chunk], *, min_overlap: int) -> list[Section]:
    section_groups: dict[str, list[Chunk]] = defaultdict(list)
    for chunk in chunks:
        if chunk.category != "text_section" or not chunk.section_slug:
            continue
        section_groups[chunk.section_slug].append(chunk)

    sections: list[Section] = []
    sortable_groups: list[tuple[int, str, list[Chunk]]] = []
    section_slugs_by_work = collect_text_section_slugs_by_work(chunks)
    for section_key, group_chunks in section_groups.items():
        group_chunks = sorted(group_chunks, key=lambda c: c.chunk_index)
        section_order = resolve_section_order(group_chunks[0], section_slugs_by_work) or 0
        sortable_groups.append((section_order, section_key, group_chunks))

    for _, section_key, group_chunks in sorted(sortable_groups):
        merged_content, warning_count = merge_chunk_texts(
            [chunk.content for chunk in group_chunks],
            min_overlap=min_overlap,
        )
        content_type = map_content_type([chunk.content_type_raw for chunk in group_chunks])
        section_title = first_non_empty(chunk.section_title for chunk in group_chunks) or humanize_slug(section_key)
        section_number = resolve_section_order(group_chunks[0], section_slugs_by_work) or len(sections) + 1
        sections.append(
            Section(
                number=section_number,
                section_key=section_key,
                title=section_title,
                content_type=content_type,
                content=merged_content,
                chunk_count=len(group_chunks),
                overlap_count=sum(1 for chunk in group_chunks if chunk.has_overlap),
                merge_warning_count=warning_count,
            )
        )
    return sections


def merge_chunk_texts(texts: list[str], *, min_overlap: int) -> tuple[str, int]:
    cleaned = [normalize_content(text) for text in texts if normalize_content(text)]
    if not cleaned:
        return "", 0

    merged = cleaned[0]
    warnings = 0
    for text in cleaned[1:]:
        if text in merged:
            continue
        overlap = find_exact_overlap(merged, text, min_overlap=min_overlap)
        if overlap >= min_overlap:
            merged = merged + text[overlap:]
            continue

        # Fallback for chunks that start inside the current text but not at its suffix.
        anchor = longest_prefix_found_inside(merged, text, min_overlap=min_overlap)
        if anchor >= min_overlap:
            tail = text[anchor:]
            if tail and tail not in merged:
                merged = merged + tail
            continue

        warnings += 1
        separator = "\n\n" if not merged.endswith("\n") else "\n"
        merged = merged + separator + text

    return merged.strip(), warnings


def normalize_content(text: str) -> str:
    text = text.replace("\r\n", "\n").replace("\r", "\n").strip()
    text = re.sub(r"\n{3,}", "\n\n", text)
    return text


def find_exact_overlap(left: str, right: str, *, min_overlap: int) -> int:
    max_len = min(len(left), len(right))
    for size in range(max_len, min_overlap - 1, -1):
        if left[-size:] == right[:size]:
            return size
    return 0


def longest_prefix_found_inside(left: str, right: str, *, min_overlap: int) -> int:
    max_len = min(len(left), len(right))
    for size in range(max_len, min_overlap - 1, -1):
        prefix = right[:size]
        pos = left.rfind(prefix)
        if pos >= 0:
            return size
    return 0


def map_content_type(types: list[str]) -> str:
    normalized = {normalize_content_type(t) for t in types if t}
    if not normalized:
        return "PROSE"
    if len(normalized) == 1:
        value = next(iter(normalized))
        return value if value in CONTENT_TYPE_VALUES else "MIXED"
    return "MIXED"


def normalize_content_type(value: Any) -> str:
    text = str(value or "").strip()
    lowered = text.lower()
    if lowered == "poem":
        return "POETRY"
    if lowered == "prose":
        return "PROSE"
    return text.upper()


def humanize_slug(value: str) -> str:
    return value.replace("-", " ").strip().title()


def first_non_empty(values: Any) -> str | None:
    for value in values:
        if value:
            return str(value)
    return None


def first_or_none(values: list[str]) -> str | None:
    return values[0] if values else None


def to_int_or_none(value: Any) -> int | None:
    if value is None or value == "":
        return None
    try:
        return int(value)
    except (TypeError, ValueError):
        return None


def nullable_str(value: Any) -> str | None:
    if value is None:
        return None
    text = str(value).strip()
    return text or None


def count_words(text: str) -> int:
    return len(re.findall(r"\w+", text, flags=re.UNICODE))


def build_summary(title: str, title_meaning: str | None) -> str | None:
    if title_meaning:
        return title_meaning
    return f"Dữ liệu đọc và phân tích tác phẩm {title}."


def print_report(
    seeds: list[WorkSeed],
    chunks: list[Chunk],
    files: list[str],
    *,
    preview: bool,
) -> None:
    print("Literature import dry-run report")
    print("=" * 34)
    print("Files:")
    for path in files:
        print(f"- {os.path.relpath(path, ROOT)}")
    print(f"Rows parsed: {len(chunks)}")
    print(f"Works found: {len(seeds)}")

    ready = [seed for seed in seeds if seed.sections]
    missing = [seed for seed in seeds if not seed.sections]
    print(f"Ready to import: {len(ready)}")
    print(f"Missing readingSections: {len(missing)}")
    if missing:
        print("  " + ", ".join(seed.title for seed in missing))

    print("\nPer-work detail:")
    for seed in seeds:
        status = "READY" if seed.sections else "MISSING_SECTIONS"
        value_flags = [
            f"realistic={'yes' if seed.realistic_value else 'no'}",
            f"humanistic={'yes' if seed.humanistic_value else 'no'}",
            f"artistic={'yes' if seed.artistic_value else 'no'}",
        ]
        extras = (
            " extra_commentary_categories="
            + ",".join(sorted(seed.extra_commentary_content.keys()))
            if seed.extra_commentary_content
            else ""
        )
        print(
            f"- [{status}] {seed.title} ({seed.slug}) "
            f"author={seed.author_name} genre={seed.genre}/{seed.sub_genre or '-'} "
            f"period={seed.period} grade={seed.grade} semester={seed.semester} "
            f"sections={len(seed.sections)} " + " ".join(value_flags) + extras
        )
        for section in seed.sections:
            warn = f", merge_warnings={section.merge_warning_count}" if section.merge_warning_count else ""
            print(
                f"    {section.number}. {section.title} "
                f"[{section.content_type}] chunks={section.chunk_count}, "
                f"overlap_chunks={section.overlap_count}, words={section.word_count}{warn}"
            )
            if preview:
                preview_text = re.sub(r"\s+", " ", section.content).strip()
                print(f"       preview: {preview_text[:220]}")


def apply_to_db(seeds: list[WorkSeed], *, args: argparse.Namespace) -> None:
    db = get_db_module()
    params = get_db_params(args)

    if db.__name__ == "psycopg2":
        conn = db.connect(**params)
    else:
        conn = db.connect(**params)

    try:
        with conn:
            with conn.cursor() as cur:
                for seed in seeds:
                    validate_seed_for_db(seed)
                    author_id = upsert_author(cur, seed)
                    work_id = upsert_work(cur, seed, author_id)
                    if args.replace:
                        if args.include_commentaries:
                            cur.execute("DELETE FROM work_commentaries WHERE work_id = %s", (work_id,))
                        cur.execute("DELETE FROM work_sections WHERE work_id = %s", (work_id,))
                    for section in seed.sections:
                        upsert_section(cur, work_id, section)
                    if args.include_commentaries:
                        upsert_commentaries(cur, work_id, seed)
        print(f"\nApplied import for {len(seeds)} work(s).")
    finally:
        conn.close()


def get_db_module():
    try:
        import psycopg2  # type: ignore

        return psycopg2
    except ImportError:
        try:
            import psycopg  # type: ignore

            return psycopg
        except ImportError as exc:
            raise SystemExit(
                "Apply mode needs a PostgreSQL driver. Install psycopg2-binary or psycopg, "
                "or run without --apply for dry-run."
            ) from exc


def get_db_params(args: argparse.Namespace) -> dict[str, Any]:
    return {
        "host": args.db_host or os.getenv("DB_HOST") or "localhost",
        "port": args.db_port or int(os.getenv("DB_PORT") or 5432),
        "dbname": args.db_name or os.getenv("DB_NAME") or "lexilearn",
        "user": args.db_user or os.getenv("DB_USERNAME") or os.getenv("DB_USER") or "postgres",
        "password": args.db_password or os.getenv("DB_PASSWORD") or "root",
    }


def validate_seed_for_db(seed: WorkSeed) -> None:
    if seed.author_period not in PERIOD_VALUES:
        raise ValueError(f"Invalid author period for {seed.title}: {seed.author_period}")
    if seed.period not in PERIOD_VALUES:
        raise ValueError(f"Invalid work period for {seed.title}: {seed.period}")
    if seed.grade is not None and seed.grade not in {10, 11, 12}:
        raise ValueError(f"Invalid grade for {seed.title}: {seed.grade}")
    if seed.semester is not None and seed.semester not in {1, 2}:
        raise ValueError(f"Invalid semester for {seed.title}: {seed.semester}")
    for section in seed.sections:
        if section.content_type not in CONTENT_TYPE_VALUES:
            raise ValueError(f"Invalid contentType for {seed.title}/{section.title}: {section.content_type}")


def upsert_author(cur: Any, seed: WorkSeed) -> str:
    cur.execute(
        """
        INSERT INTO authors (
            name, pen_name, slug, birth_year, death_year, period, bio, portrait_url, created_at, updated_at
        )
        VALUES (%s, NULL, %s, NULL, NULL, %s, %s, NULL, NOW(), NOW())
        ON CONFLICT (slug) DO UPDATE SET
            name = EXCLUDED.name,
            period = EXCLUDED.period,
            bio = EXCLUDED.bio,
            updated_at = NOW()
        RETURNING id
        """,
        (seed.author_name, seed.author_slug, seed.author_period, seed.author_bio),
    )
    return str(cur.fetchone()[0])


def upsert_work(cur: Any, seed: WorkSeed, author_id: str) -> str:
    cur.execute(
        """
        INSERT INTO works (
            author_id, title, slug, original_title, genre, sub_genre, period,
            grade, semester, publish_year, summary, cover_url, is_published, view_count,
            historical_context, realistic_value, humanistic_value, artistic_value,
            famous_quote, quote_attribution, created_at, updated_at
        )
        VALUES (
            %s, %s, %s, NULL, %s, %s, %s,
            %s, %s, %s, %s, NULL, TRUE, 0,
            %s, %s, %s, %s,
            NULL, NULL, NOW(), NOW()
        )
        ON CONFLICT (slug) DO UPDATE SET
            author_id = EXCLUDED.author_id,
            title = EXCLUDED.title,
            genre = EXCLUDED.genre,
            sub_genre = EXCLUDED.sub_genre,
            period = EXCLUDED.period,
            grade = EXCLUDED.grade,
            semester = EXCLUDED.semester,
            publish_year = EXCLUDED.publish_year,
            summary = EXCLUDED.summary,
            is_published = TRUE,
            historical_context = EXCLUDED.historical_context,
            realistic_value = EXCLUDED.realistic_value,
            humanistic_value = EXCLUDED.humanistic_value,
            artistic_value = EXCLUDED.artistic_value,
            updated_at = NOW()
        RETURNING id
        """,
        (
            author_id,
            seed.title,
            seed.slug,
            seed.genre,
            seed.sub_genre,
            seed.period,
            seed.grade,
            seed.semester,
            seed.publish_year,
            seed.summary,
            seed.historical_context,
            seed.realistic_value,
            seed.humanistic_value,
            seed.artistic_value,
        ),
    )
    return str(cur.fetchone()[0])


def upsert_section(cur: Any, work_id: str, section: Section) -> None:
    cur.execute(
        """
        INSERT INTO work_sections (
            work_id, number, title, content, word_count, content_type, created_at, updated_at
        )
        VALUES (%s, %s, %s, %s, %s, %s, NOW(), NOW())
        ON CONFLICT (work_id, number) DO UPDATE SET
            title = EXCLUDED.title,
            content = EXCLUDED.content,
            word_count = EXCLUDED.word_count,
            content_type = EXCLUDED.content_type,
            updated_at = NOW()
        """,
        (
            work_id,
            section.number,
            section.title,
            section.content,
            section.word_count,
            section.content_type,
        ),
    )


def upsert_commentaries(cur: Any, work_id: str, seed: WorkSeed) -> None:
    commentaries = build_commentaries(seed)
    for index, commentary in enumerate(commentaries):
        cur.execute(
            """
            INSERT INTO work_commentaries (
                work_id, title, content, commentator_name, commentator_type,
                source_title, source_url, published_year, display_order,
                is_featured, is_published, created_at, updated_at
            )
            VALUES (%s, %s, %s, 'LexiLearn Editorial', 'EDITORIAL',
                    NULL, NULL, NULL, %s, %s, TRUE, NOW(), NOW())
            ON CONFLICT (work_id, display_order) DO UPDATE SET
                title = EXCLUDED.title,
                content = EXCLUDED.content,
                commentator_name = EXCLUDED.commentator_name,
                commentator_type = EXCLUDED.commentator_type,
                is_featured = EXCLUDED.is_featured,
                is_published = TRUE,
                updated_at = NOW()
            """,
            (
                work_id,
                commentary["title"],
                commentary["content"],
                index,
                index == 0,
            ),
        )


def build_commentaries(seed: WorkSeed) -> list[dict[str, str]]:
    items: list[dict[str, str]] = []
    if seed.title_meaning:
        items.append({"title": "Ý nghĩa nhan đề", "content": seed.title_meaning})
    if seed.layout_analysis:
        items.append({"title": "Bố cục tác phẩm", "content": "\n\n".join(seed.layout_analysis)})
    values = [
        ("Giá trị hiện thực", seed.realistic_value),
        ("Giá trị nhân đạo", seed.humanistic_value),
        ("Giá trị nghệ thuật", seed.artistic_value),
    ]
    content = "\n\n".join(f"{title}: {value}" for title, value in values if value)
    if content:
        items.append({"title": "Tổng kết giá trị", "content": content})
    for category, values in sorted(seed.extra_commentary_content.items()):
        if not values:
            continue
        items.append(
            {
                "title": humanize_slug(category),
                "content": "\n\n".join(values),
            }
        )
    return items


if __name__ == "__main__":
    raise SystemExit(main())
