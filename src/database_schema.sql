CREATE TABLE osm_maynooth.osm_user
(
  user_id integer NOT NULL,
  user_name character varying(20),
  CONSTRAINT osm_user_pkey PRIMARY KEY (user_id)
)

CREATE TABLE osm_maynooth.osm_object(
			object_key BIGSERIAL PRIMARY KEY,
			osm_type CHAR(1),
			osm_id INTEGER,
			osm_version INTEGER,
			corodinates TEXT,
			timestamp TIMESTAMP, 
			user_id INTEGER REFERENCES osm_maynooth.osm_user(user_id),
			visible boolean,
			CONSTRAINT osm_object_unique UNIQUE (osm_type, osm_id, osm_version));

INSERT INTO osm_maynooth.osm_user(user_id, user_name) VALUES (1, 'rrdc');

INSERT INTO osm_maynooth.osm_object(osm_type, osm_id, osm_version, timestamp, user_id, visible) 
	VALUES('N', 1, 2, '2016-10-31 10:11:12', 1, true);

select * from osm_maynooth.osm_object;

CREATE TABLE osm_maynooth.osm_
