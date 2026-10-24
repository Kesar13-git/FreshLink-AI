create table if not exists public.analysis_history (
    id varchar(36) primary key,
    produce varchar(100) not null,
    freshness_score integer not null check (freshness_score between 0 and 100),
    quality_stage varchar(50) not null,
    quality_window varchar(50) not null,
    reasons json not null,
    created_at timestamptz not null default now()
);

create index if not exists ix_analysis_history_created_at
    on public.analysis_history (created_at desc);

create table if not exists public.inventory_items (
    id varchar(36) primary key,
    name varchar(100) not null,
    category varchar(100) not null,
    stock_units integer not null check (stock_units >= 0),
    unit varchar(30) not null,
    current_price double precision not null check (current_price >= 0),
    quality_score integer not null check (quality_score between 0 and 100),
    quality_window_days integer not null check (quality_window_days >= 0),
    avg_daily_demand double precision not null check (avg_daily_demand >= 0),
    reorder_point integer not null check (reorder_point >= 0),
    supplier varchar(150) not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);
