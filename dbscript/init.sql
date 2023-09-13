create database if not exists data_collect;
use data_collect;
CREATE TABLE IF NOT EXISTS ods_data (uuid String , is_encryption Int8, source_type String, data_type String, record_time Nullable(DateTime), create_time DateTime, content String)engine=MergeTree primary key (uuid);