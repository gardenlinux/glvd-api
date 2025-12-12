--
-- PostgreSQL database dump
--

\restrict ltHAAaDD09X3APY68zF5zSf3DwDla61MXTY4xXcVBpz2RdTc1F3ArAcAoaKr6yv

-- Dumped from database version 18.1 (Debian 18.1-1.pgdg13+2)
-- Dumped by pg_dump version 18.1 (Debian 18.1-1.pgdg13+2)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: debversion; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS debversion WITH SCHEMA public;


--
-- Name: EXTENSION debversion; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION debversion IS 'Debian version number data type';


--
-- Name: assert_latest_migration(integer); Type: FUNCTION; Schema: public; Owner: glvd
--

CREATE FUNCTION public.assert_latest_migration(id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE
    latest_id integer;
BEGIN
    SELECT
        MAX(migrations.id) INTO latest_id
    FROM
        migrations;
    ASSERT latest_id = id,
    'migration assertion ' || id || ' failed, current latest is ' || latest_id;
    RETURN;
END;
$$;


ALTER FUNCTION public.assert_latest_migration(id integer) OWNER TO glvd;

--
-- Name: log_migration(integer); Type: FUNCTION; Schema: public; Owner: glvd
--

CREATE FUNCTION public.log_migration(id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO "migrations" (id)
        VALUES (id);
END;
$$;


ALTER FUNCTION public.log_migration(id integer) OWNER TO glvd;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: all_cve; Type: TABLE; Schema: public; Owner: glvd
--

CREATE TABLE public.all_cve (
    cve_id text NOT NULL,
    last_mod timestamp with time zone DEFAULT now() NOT NULL,
    data json NOT NULL
);


ALTER TABLE public.all_cve OWNER TO glvd;

--
-- Name: cve_context; Type: TABLE; Schema: public; Owner: glvd
--

CREATE TABLE public.cve_context (
    dist_id integer NOT NULL,
    gardenlinux_version text,
    cve_id text NOT NULL,
    create_date timestamp with time zone DEFAULT now() NOT NULL,
    use_case text CONSTRAINT cve_context_context_descriptor_not_null NOT NULL,
    score_override numeric,
    description text NOT NULL,
    is_resolved boolean DEFAULT false,
    id integer NOT NULL,
    triaged boolean DEFAULT false
);


ALTER TABLE public.cve_context OWNER TO glvd;

--
-- Name: cve_context_id_seq; Type: SEQUENCE; Schema: public; Owner: glvd
--

ALTER TABLE public.cve_context ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.cve_context_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: cve_context_kernel; Type: TABLE; Schema: public; Owner: glvd
--

CREATE TABLE public.cve_context_kernel (
    cve_id text NOT NULL,
    lts_version text NOT NULL,
    fixed_version text,
    is_fixed boolean NOT NULL,
    is_relevant_subsystem boolean NOT NULL,
    source_data jsonb NOT NULL
);


ALTER TABLE public.cve_context_kernel OWNER TO glvd;

--
-- Name: cve_with_context; Type: VIEW; Schema: public; Owner: glvd
--

CREATE VIEW public.cve_with_context AS
 SELECT dist_id,
    cve_id
   FROM public.cve_context
  GROUP BY dist_id, cve_id;


ALTER VIEW public.cve_with_context OWNER TO glvd;

--
-- Name: cvedetails; Type: VIEW; Schema: public; Owner: glvd
--

CREATE VIEW public.cvedetails AS
SELECT
    NULL::text AS cve_id,
    NULL::json AS vulnstatus,
    NULL::json AS published,
    NULL::json AS modified,
    NULL::timestamp with time zone AS ingested,
    NULL::text[] AS cve_context_description,
    NULL::text[] AS distro,
    NULL::text[] AS distro_version,
    NULL::boolean[] AS is_vulnerable,
    NULL::text[] AS source_package_name,
    NULL::text[] AS source_package_version,
    NULL::text[] AS version_fixed,
    NULL::json AS description,
    NULL::numeric AS base_score_v40,
    NULL::numeric AS base_score_v31,
    NULL::numeric AS base_score_v30,
    NULL::numeric AS base_score_v2,
    NULL::text AS vector_string_v40,
    NULL::text AS vector_string_v31,
    NULL::text AS vector_string_v30,
    NULL::text AS vector_string_v2;


ALTER VIEW public.cvedetails OWNER TO glvd;

--
-- Name: deb_cve; Type: TABLE; Schema: public; Owner: glvd
--

CREATE TABLE public.deb_cve (
    dist_id integer NOT NULL,
    gardenlinux_version text,
    cve_id text NOT NULL,
    last_mod timestamp with time zone DEFAULT now() NOT NULL,
    cvss_severity integer,
    deb_source text NOT NULL,
    deb_version public.debversion NOT NULL,
    deb_version_fixed public.debversion,
    debsec_vulnerable boolean NOT NULL,
    data_cpe_match json NOT NULL
);


ALTER TABLE public.deb_cve OWNER TO glvd;

--
-- Name: debsec_cve; Type: TABLE; Schema: public; Owner: glvd
--

CREATE TABLE public.debsec_cve (
    dist_id integer NOT NULL,
    gardenlinux_version text,
    cve_id text NOT NULL,
    last_mod timestamp with time zone DEFAULT now() NOT NULL,
    deb_source text NOT NULL,
    deb_version_fixed public.debversion,
    debsec_tag text,
    debsec_note text,
    minor_deb_version_fixed text
);


ALTER TABLE public.debsec_cve OWNER TO glvd;

--
-- Name: debsrc; Type: TABLE; Schema: public; Owner: glvd
--

CREATE TABLE public.debsrc (
    dist_id integer NOT NULL,
    gardenlinux_version text,
    last_mod timestamp with time zone DEFAULT now() NOT NULL,
    deb_source text NOT NULL,
    deb_version public.debversion NOT NULL,
    minor_deb_version text
);


ALTER TABLE public.debsrc OWNER TO glvd;

--
-- Name: dist_cpe; Type: TABLE; Schema: public; Owner: glvd
--

CREATE TABLE public.dist_cpe (
    id integer NOT NULL,
    cpe_vendor text NOT NULL,
    cpe_product text NOT NULL,
    cpe_version text NOT NULL,
    deb_codename text NOT NULL
);


ALTER TABLE public.dist_cpe OWNER TO glvd;

--
-- Name: dist_cpe_id_seq; Type: SEQUENCE; Schema: public; Owner: glvd
--

ALTER TABLE public.dist_cpe ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.dist_cpe_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: image_package; Type: TABLE; Schema: public; Owner: glvd
--

CREATE TABLE public.image_package (
    image_variant_id bigint NOT NULL,
    package_name text NOT NULL
);


ALTER TABLE public.image_package OWNER TO glvd;

--
-- Name: image_variant; Type: TABLE; Schema: public; Owner: glvd
--

CREATE TABLE public.image_variant (
    id bigint NOT NULL,
    namespace text DEFAULT 'gardenlinux'::text NOT NULL,
    image_name text NOT NULL,
    image_version text NOT NULL,
    commit_id text,
    metadata jsonb DEFAULT '{}'::jsonb,
    packages text[] DEFAULT '{}'::text[]
);


ALTER TABLE public.image_variant OWNER TO glvd;

--
-- Name: image_variant_id_seq; Type: SEQUENCE; Schema: public; Owner: glvd
--

CREATE SEQUENCE public.image_variant_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.image_variant_id_seq OWNER TO glvd;

--
-- Name: image_variant_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: glvd
--

ALTER SEQUENCE public.image_variant_id_seq OWNED BY public.image_variant.id;


--
-- Name: imagesourcepackagecve; Type: VIEW; Schema: public; Owner: glvd
--

CREATE VIEW public.imagesourcepackagecve AS
 SELECT DISTINCT all_cve.cve_id,
    deb_cve.deb_source AS source_package_name,
    deb_cve.deb_version AS source_package_version,
    dist_cpe.cpe_version AS gardenlinux_version,
    iv.namespace AS gardenlinux_image_namespace,
    iv.image_name AS gardenlinux_image_name,
    iv.image_version AS gardenlinux_image_version,
    iv.commit_id AS gardenlinux_image_commit_id,
    ((deb_cve.debsec_vulnerable AND (cve_context.is_resolved IS NOT TRUE)) = true) AS is_vulnerable,
    deb_cve.debsec_vulnerable,
    cve_context.is_resolved,
    (all_cve.data ->> 'published'::text) AS cve_published_date,
    (all_cve.data ->> 'lastModified'::text) AS cve_last_modified_date,
    all_cve.last_mod AS cve_last_ingested_date,
        CASE
            WHEN (((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric IS NOT NULL) THEN ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric
            WHEN (((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric IS NOT NULL) THEN ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric
            WHEN (((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric IS NOT NULL) THEN ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric
            WHEN (((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric IS NOT NULL) THEN ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric
            ELSE NULL::numeric
        END AS base_score,
        CASE
            WHEN ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) IS NOT NULL) THEN (((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text)
            WHEN ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) IS NOT NULL) THEN (((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text)
            WHEN ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) IS NOT NULL) THEN (((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text)
            WHEN ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) IS NOT NULL) THEN (((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text)
            ELSE NULL::text
        END AS vector_string,
    ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric AS base_score_v40,
    ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric AS base_score_v31,
    ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric AS base_score_v30,
    ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric AS base_score_v2,
    (((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) AS vector_string_v40,
    (((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) AS vector_string_v31,
    (((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) AS vector_string_v30,
    (((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) AS vector_string_v2,
    (all_cve.data ->> 'vulnStatus'::text) AS vuln_status
   FROM (((((public.all_cve
     JOIN public.deb_cve USING (cve_id))
     JOIN public.dist_cpe ON ((deb_cve.dist_id = dist_cpe.id)))
     FULL JOIN public.cve_context USING (cve_id, dist_id))
     JOIN public.image_package ip ON ((ip.package_name = deb_cve.deb_source)))
     JOIN public.image_variant iv ON ((iv.id = ip.image_variant_id)))
  WHERE ((dist_cpe.cpe_product = 'gardenlinux'::text) AND (iv.namespace = 'gardenlinux'::text) AND (deb_cve.debsec_vulnerable = true) AND (deb_cve.deb_source <> 'linux'::text) AND (dist_cpe.cpe_version = iv.image_version));


ALTER VIEW public.imagesourcepackagecve OWNER TO glvd;

--
-- Name: kernel_vulns; Type: VIEW; Schema: public; Owner: glvd
--

CREATE VIEW public.kernel_vulns AS
 SELECT all_cve.cve_id,
    deb_cve.deb_source AS source_package_name,
    deb_cve.deb_version AS source_package_version,
    dist_cpe.cpe_version AS gardenlinux_version,
    ((((deb_cve.deb_version OPERATOR(public.<) (cve_context_kernel.fixed_version)::public.debversion) OR (cve_context_kernel.fixed_version IS NULL)) AND (cve_context_kernel.is_relevant_subsystem IS TRUE) AND (cve_context.is_resolved IS NOT TRUE)) = true) AS is_vulnerable,
    cve_context.is_resolved,
    cve_context_kernel.is_relevant_subsystem,
    cve_context_kernel.lts_version,
    (cve_context_kernel.fixed_version)::public.debversion AS fixed_version,
    cve_context_kernel.is_fixed
   FROM ((((public.all_cve
     JOIN public.deb_cve USING (cve_id))
     JOIN public.dist_cpe ON ((deb_cve.dist_id = dist_cpe.id)))
     FULL JOIN public.cve_context USING (cve_id, dist_id))
     JOIN public.cve_context_kernel cve_context_kernel(cve_id_1, lts_version, fixed_version, is_fixed, is_relevant_subsystem, source_data) ON (((all_cve.cve_id = cve_context_kernel.cve_id_1) AND (cve_context_kernel.lts_version = concat(split_part((deb_cve.deb_version)::text, '.'::text, 1), '.', split_part((deb_cve.deb_version)::text, '.'::text, 2))))))
  WHERE ((dist_cpe.cpe_product = 'gardenlinux'::text) AND ((((deb_cve.deb_version OPERATOR(public.<) (cve_context_kernel.fixed_version)::public.debversion) OR (cve_context_kernel.fixed_version IS NULL)) AND (cve_context_kernel.is_relevant_subsystem IS TRUE)) = true) AND (deb_cve.deb_source = 'linux'::text));


ALTER VIEW public.kernel_vulns OWNER TO glvd;

--
-- Name: nvd_cve; Type: TABLE; Schema: public; Owner: glvd
--

CREATE TABLE public.nvd_cve (
    cve_id text NOT NULL,
    last_mod timestamp with time zone NOT NULL,
    data json NOT NULL
);


ALTER TABLE public.nvd_cve OWNER TO glvd;

--
-- Name: kernel_cve; Type: VIEW; Schema: public; Owner: glvd
--

CREATE VIEW public.kernel_cve AS
 SELECT k.cve_id,
    k.source_package_name,
    k.source_package_version,
    k.lts_version,
    k.gardenlinux_version,
    k.is_vulnerable,
    k.fixed_version,
    (nvd.data ->> 'published'::text) AS cve_published_date,
    (nvd.data ->> 'lastModified'::text) AS cve_last_modified_date,
    nvd.last_mod AS cve_last_ingested_date,
        CASE
            WHEN (((((((nvd.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric IS NOT NULL) THEN ((((((nvd.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric
            WHEN (((((((nvd.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric IS NOT NULL) THEN ((((((nvd.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric
            WHEN (((((((nvd.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric IS NOT NULL) THEN ((((((nvd.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric
            WHEN (((((((nvd.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric IS NOT NULL) THEN ((((((nvd.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric
            ELSE NULL::numeric
        END AS base_score,
        CASE
            WHEN ((((((nvd.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) IS NOT NULL) THEN (((((nvd.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text)
            WHEN ((((((nvd.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) IS NOT NULL) THEN (((((nvd.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text)
            WHEN ((((((nvd.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) IS NOT NULL) THEN (((((nvd.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text)
            WHEN ((((((nvd.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) IS NOT NULL) THEN (((((nvd.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text)
            ELSE NULL::text
        END AS vector_string,
    ((((((nvd.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric AS base_score_v40,
    ((((((nvd.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric AS base_score_v31,
    ((((((nvd.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric AS base_score_v30,
    ((((((nvd.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric AS base_score_v2,
    (((((nvd.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) AS vector_string_v40,
    (((((nvd.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) AS vector_string_v31,
    (((((nvd.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) AS vector_string_v30,
    (((((nvd.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) AS vector_string_v2,
    (nvd.data ->> 'vulnStatus'::text) AS vuln_status
   FROM (public.kernel_vulns k
     JOIN public.nvd_cve nvd USING (cve_id));


ALTER VIEW public.kernel_cve OWNER TO glvd;

--
-- Name: kernel_cvedetails; Type: VIEW; Schema: public; Owner: glvd
--

CREATE VIEW public.kernel_cvedetails AS
SELECT
    NULL::text AS cve_id,
    NULL::json AS vulnstatus,
    NULL::json AS description,
    NULL::json AS published,
    NULL::json AS modified,
    NULL::timestamp with time zone AS ingested,
    NULL::text[] AS cve_context_description,
    NULL::text[] AS lts_version,
    NULL::text[] AS fixed_version,
    NULL::boolean[] AS is_fixed,
    NULL::boolean[] AS is_relevant_subsystem,
    NULL::numeric AS base_score_v40,
    NULL::numeric AS base_score_v31,
    NULL::numeric AS base_score_v30,
    NULL::numeric AS base_score_v2,
    NULL::text AS vector_string_v40,
    NULL::text AS vector_string_v31,
    NULL::text AS vector_string_v30,
    NULL::text AS vector_string_v2;


ALTER VIEW public.kernel_cvedetails OWNER TO glvd;

--
-- Name: migrations; Type: TABLE; Schema: public; Owner: glvd
--

CREATE TABLE public.migrations (
    id integer NOT NULL,
    migrated_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.migrations OWNER TO glvd;

--
-- Name: sourcepackagecve; Type: VIEW; Schema: public; Owner: glvd
--

CREATE VIEW public.sourcepackagecve AS
 SELECT all_cve.cve_id,
    deb_cve.deb_source AS source_package_name,
    deb_cve.deb_version AS source_package_version,
    dist_cpe.cpe_version AS gardenlinux_version,
    ((deb_cve.debsec_vulnerable AND (cve_context.is_resolved IS NOT TRUE)) = true) AS is_vulnerable,
    deb_cve.debsec_vulnerable,
    cve_context.is_resolved,
    (all_cve.data ->> 'published'::text) AS cve_published_date,
    (all_cve.data ->> 'lastModified'::text) AS cve_last_modified_date,
    all_cve.last_mod AS cve_last_ingested_date,
        CASE
            WHEN (((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric IS NOT NULL) THEN ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric
            WHEN (((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric IS NOT NULL) THEN ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric
            WHEN (((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric IS NOT NULL) THEN ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric
            WHEN (((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric IS NOT NULL) THEN ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric
            ELSE NULL::numeric
        END AS base_score,
        CASE
            WHEN ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) IS NOT NULL) THEN (((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text)
            WHEN ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) IS NOT NULL) THEN (((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text)
            WHEN ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) IS NOT NULL) THEN (((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text)
            WHEN ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) IS NOT NULL) THEN (((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text)
            ELSE NULL::text
        END AS vector_string,
    ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric AS base_score_v40,
    ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric AS base_score_v31,
    ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric AS base_score_v30,
    ((((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric AS base_score_v2,
    (((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) AS vector_string_v40,
    (((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) AS vector_string_v31,
    (((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) AS vector_string_v30,
    (((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) AS vector_string_v2,
    (all_cve.data ->> 'vulnStatus'::text) AS vuln_status
   FROM (((public.all_cve
     JOIN public.deb_cve USING (cve_id))
     JOIN public.dist_cpe ON ((deb_cve.dist_id = dist_cpe.id)))
     FULL JOIN public.cve_context USING (cve_id, dist_id))
  WHERE ((dist_cpe.cpe_product = 'gardenlinux'::text) AND (deb_cve.debsec_vulnerable = true) AND (deb_cve.deb_source <> 'linux'::text));


ALTER VIEW public.sourcepackagecve OWNER TO glvd;

--
-- Name: recentsourcepackagecve; Type: VIEW; Schema: public; Owner: glvd
--

CREATE VIEW public.recentsourcepackagecve AS
 SELECT cve_id,
    source_package_name,
    source_package_version,
    gardenlinux_version,
    is_vulnerable,
    cve_published_date,
    base_score,
    vector_string,
    base_score_v40,
    base_score_v31,
    base_score_v30,
    base_score_v2,
    vector_string_v40,
    vector_string_v31,
    vector_string_v30,
    vector_string_v2
   FROM public.sourcepackagecve
  WHERE ((cve_published_date)::timestamp with time zone > (now() - '10 days'::interval));


ALTER VIEW public.recentsourcepackagecve OWNER TO glvd;

--
-- Name: sourcepackage; Type: VIEW; Schema: public; Owner: glvd
--

CREATE VIEW public.sourcepackage AS
 SELECT debsrc.deb_source AS source_package_name,
    debsrc.deb_version AS source_package_version,
    dist_cpe.cpe_version AS gardenlinux_version
   FROM (public.debsrc
     JOIN public.dist_cpe ON ((debsrc.dist_id = dist_cpe.id)))
  WHERE (dist_cpe.cpe_product = 'gardenlinux'::text);


ALTER VIEW public.sourcepackage OWNER TO glvd;

--
-- Name: sourcepackagecve_anyimage; Type: VIEW; Schema: public; Owner: glvd
--

CREATE VIEW public.sourcepackagecve_anyimage AS
 SELECT DISTINCT cve_id,
    source_package_name,
    source_package_version,
    gardenlinux_version,
    is_vulnerable,
    debsec_vulnerable,
    is_resolved,
    cve_published_date,
    cve_last_modified_date,
    cve_last_ingested_date,
    base_score,
    vector_string,
    base_score_v40,
    base_score_v31,
    base_score_v30,
    base_score_v2,
    vector_string_v40,
    vector_string_v31,
    vector_string_v30,
    vector_string_v2,
    vuln_status
   FROM public.imagesourcepackagecve;


ALTER VIEW public.sourcepackagecve_anyimage OWNER TO glvd;

--
-- Name: triage; Type: VIEW; Schema: public; Owner: glvd
--

CREATE VIEW public.triage AS
 SELECT deb_cve.cve_id,
    deb_cve.deb_source AS source_package_name,
    deb_cve.deb_version AS source_package_version,
    cve_context.is_resolved AS triage_marked_as_resolved,
    cve_context.create_date AS triage_date,
    cve_context.use_case AS triage_use_case,
    cve_context.description AS triage_description,
    cve_context.gardenlinux_version AS triage_gardenlinux_version,
    (nvd_cve.data -> 'vulnStatus'::text) AS nvd_vulnerability_status,
    (nvd_cve.data -> 'published'::text) AS nvd_cve_published_date,
    (nvd_cve.data -> 'lastModified'::text) AS nvd_cve_last_modified_date,
    (((nvd_cve.data -> 'descriptions'::text) -> 0) -> 'value'::text) AS nvd_cve_description,
        CASE
            WHEN (((((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric IS NOT NULL) THEN ((((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric
            WHEN (((((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric IS NOT NULL) THEN ((((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric
            WHEN (((((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric IS NOT NULL) THEN ((((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric
            WHEN (((((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric IS NOT NULL) THEN ((((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric
            ELSE NULL::numeric
        END AS nvd_cve_cvss_base_score
   FROM ((public.deb_cve
     JOIN public.nvd_cve USING (cve_id))
     JOIN public.cve_context USING (cve_id, gardenlinux_version));


ALTER VIEW public.triage OWNER TO glvd;

--
-- Name: image_variant id; Type: DEFAULT; Schema: public; Owner: glvd
--

ALTER TABLE ONLY public.image_variant ALTER COLUMN id SET DEFAULT nextval('public.image_variant_id_seq'::regclass);


--
-- Name: all_cve all_cve_pkey; Type: CONSTRAINT; Schema: public; Owner: glvd
--

ALTER TABLE ONLY public.all_cve
    ADD CONSTRAINT all_cve_pkey PRIMARY KEY (cve_id);


--
-- Name: cve_context_kernel cve_context_kernel_pkey; Type: CONSTRAINT; Schema: public; Owner: glvd
--

ALTER TABLE ONLY public.cve_context_kernel
    ADD CONSTRAINT cve_context_kernel_pkey PRIMARY KEY (cve_id, lts_version);


--
-- Name: cve_context cve_context_pkey; Type: CONSTRAINT; Schema: public; Owner: glvd
--

ALTER TABLE ONLY public.cve_context
    ADD CONSTRAINT cve_context_pkey PRIMARY KEY (id);


--
-- Name: deb_cve deb_cve_pkey; Type: CONSTRAINT; Schema: public; Owner: glvd
--

ALTER TABLE ONLY public.deb_cve
    ADD CONSTRAINT deb_cve_pkey PRIMARY KEY (dist_id, cve_id, deb_source);


--
-- Name: debsec_cve debsec_cve_pkey; Type: CONSTRAINT; Schema: public; Owner: glvd
--

ALTER TABLE ONLY public.debsec_cve
    ADD CONSTRAINT debsec_cve_pkey PRIMARY KEY (dist_id, cve_id, deb_source);


--
-- Name: debsrc debsrc_pkey; Type: CONSTRAINT; Schema: public; Owner: glvd
--

ALTER TABLE ONLY public.debsrc
    ADD CONSTRAINT debsrc_pkey PRIMARY KEY (dist_id, deb_source);


--
-- Name: dist_cpe dist_cpe_pkey; Type: CONSTRAINT; Schema: public; Owner: glvd
--

ALTER TABLE ONLY public.dist_cpe
    ADD CONSTRAINT dist_cpe_pkey PRIMARY KEY (id);


--
-- Name: image_package image_package_pkey; Type: CONSTRAINT; Schema: public; Owner: glvd
--

ALTER TABLE ONLY public.image_package
    ADD CONSTRAINT image_package_pkey PRIMARY KEY (image_variant_id, package_name);


--
-- Name: image_variant image_variant_namespace_name_version_unique; Type: CONSTRAINT; Schema: public; Owner: glvd
--

ALTER TABLE ONLY public.image_variant
    ADD CONSTRAINT image_variant_namespace_name_version_unique UNIQUE (namespace, image_name, image_version);


--
-- Name: image_variant image_variant_pkey; Type: CONSTRAINT; Schema: public; Owner: glvd
--

ALTER TABLE ONLY public.image_variant
    ADD CONSTRAINT image_variant_pkey PRIMARY KEY (id);


--
-- Name: migrations migrations_pkey; Type: CONSTRAINT; Schema: public; Owner: glvd
--

ALTER TABLE ONLY public.migrations
    ADD CONSTRAINT migrations_pkey PRIMARY KEY (id);


--
-- Name: nvd_cve nvd_cve_pkey; Type: CONSTRAINT; Schema: public; Owner: glvd
--

ALTER TABLE ONLY public.nvd_cve
    ADD CONSTRAINT nvd_cve_pkey PRIMARY KEY (cve_id);


--
-- Name: deb_cve_search; Type: INDEX; Schema: public; Owner: glvd
--

CREATE INDEX deb_cve_search ON public.deb_cve USING btree (dist_id, debsec_vulnerable, deb_source, deb_version);


--
-- Name: idx_image_package_image; Type: INDEX; Schema: public; Owner: glvd
--

CREATE INDEX idx_image_package_image ON public.image_package USING btree (image_variant_id);


--
-- Name: idx_image_package_package; Type: INDEX; Schema: public; Owner: glvd
--

CREATE INDEX idx_image_package_package ON public.image_package USING btree (package_name);


--
-- Name: idx_image_variant_packages_gin; Type: INDEX; Schema: public; Owner: glvd
--

CREATE INDEX idx_image_variant_packages_gin ON public.image_variant USING gin (packages);


--
-- Name: cvedetails _RETURN; Type: RULE; Schema: public; Owner: glvd
--

CREATE OR REPLACE VIEW public.cvedetails AS
 SELECT nvd_cve.cve_id,
    (nvd_cve.data -> 'vulnStatus'::text) AS vulnstatus,
    (nvd_cve.data -> 'published'::text) AS published,
    (nvd_cve.data -> 'lastModified'::text) AS modified,
    nvd_cve.last_mod AS ingested,
    array_agg(cve_context.description) AS cve_context_description,
    array_agg(dist_cpe.cpe_product) AS distro,
    array_agg(dist_cpe.cpe_version) AS distro_version,
    array_agg(deb_cve.debsec_vulnerable) AS is_vulnerable,
    array_agg(deb_cve.deb_source) AS source_package_name,
    array_agg((deb_cve.deb_version)::text) AS source_package_version,
    array_agg((deb_cve.deb_version_fixed)::text) AS version_fixed,
    (((nvd_cve.data -> 'descriptions'::text) -> 0) -> 'value'::text) AS description,
    ((((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric AS base_score_v40,
    ((((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric AS base_score_v31,
    ((((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric AS base_score_v30,
    ((((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric AS base_score_v2,
    (((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) AS vector_string_v40,
    (((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) AS vector_string_v31,
    (((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) AS vector_string_v30,
    (((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) AS vector_string_v2
   FROM (((public.nvd_cve
     JOIN public.deb_cve USING (cve_id))
     JOIN public.dist_cpe ON ((deb_cve.dist_id = dist_cpe.id)))
     FULL JOIN public.cve_context USING (cve_id, dist_id))
  GROUP BY nvd_cve.cve_id;


--
-- Name: kernel_cvedetails _RETURN; Type: RULE; Schema: public; Owner: glvd
--

CREATE OR REPLACE VIEW public.kernel_cvedetails AS
 SELECT nvd_cve.cve_id,
    (nvd_cve.data -> 'vulnStatus'::text) AS vulnstatus,
    (((nvd_cve.data -> 'descriptions'::text) -> 0) -> 'value'::text) AS description,
    (nvd_cve.data -> 'published'::text) AS published,
    (nvd_cve.data -> 'lastModified'::text) AS modified,
    nvd_cve.last_mod AS ingested,
    array_agg(cve_context.description) AS cve_context_description,
    array_agg(cve_context_kernel.lts_version) AS lts_version,
    array_agg(cve_context_kernel.fixed_version) AS fixed_version,
    array_agg(cve_context_kernel.is_fixed) AS is_fixed,
    array_agg(cve_context_kernel.is_relevant_subsystem) AS is_relevant_subsystem,
    ((((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric AS base_score_v40,
    ((((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric AS base_score_v31,
    ((((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric AS base_score_v30,
    ((((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'baseScore'::text))::numeric AS base_score_v2,
    (((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV40'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) AS vector_string_v40,
    (((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV31'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) AS vector_string_v31,
    (((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV30'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) AS vector_string_v30,
    (((((nvd_cve.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) AS vector_string_v2
   FROM ((public.nvd_cve
     JOIN public.cve_context_kernel USING (cve_id))
     FULL JOIN public.cve_context USING (cve_id))
  GROUP BY nvd_cve.cve_id;


--
-- Name: deb_cve deb_cve_dist_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: glvd
--

ALTER TABLE ONLY public.deb_cve
    ADD CONSTRAINT deb_cve_dist_id_fkey FOREIGN KEY (dist_id) REFERENCES public.dist_cpe(id);


--
-- Name: debsec_cve debsec_cve_dist_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: glvd
--

ALTER TABLE ONLY public.debsec_cve
    ADD CONSTRAINT debsec_cve_dist_id_fkey FOREIGN KEY (dist_id) REFERENCES public.dist_cpe(id);


--
-- Name: debsrc debsrc_dist_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: glvd
--

ALTER TABLE ONLY public.debsrc
    ADD CONSTRAINT debsrc_dist_id_fkey FOREIGN KEY (dist_id) REFERENCES public.dist_cpe(id);


--
-- Name: image_package image_package_image_variant_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: glvd
--

ALTER TABLE ONLY public.image_package
    ADD CONSTRAINT image_package_image_variant_id_fkey FOREIGN KEY (image_variant_id) REFERENCES public.image_variant(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

\unrestrict ltHAAaDD09X3APY68zF5zSf3DwDla61MXTY4xXcVBpz2RdTc1F3ArAcAoaKr6yv
