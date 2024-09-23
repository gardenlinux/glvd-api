--
-- PostgreSQL database dump
--

-- Dumped from database version 15.8 (Debian 15.8-1.pgdg120+1)
-- Dumped by pg_dump version 17rc1 (Debian 17~rc1-1)

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
-- Name: deb_cve; Type: TABLE; Schema: public; Owner: glvd
--

CREATE TABLE public.deb_cve (
    dist_id integer NOT NULL,
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
    cve_id text NOT NULL,
    last_mod timestamp with time zone DEFAULT now() NOT NULL,
    deb_source text NOT NULL,
    deb_version_fixed public.debversion,
    debsec_tag text,
    debsec_note text
);


ALTER TABLE public.debsec_cve OWNER TO glvd;

--
-- Name: debsrc; Type: TABLE; Schema: public; Owner: glvd
--

CREATE TABLE public.debsrc (
    dist_id integer NOT NULL,
    last_mod timestamp with time zone DEFAULT now() NOT NULL,
    deb_source text NOT NULL,
    deb_version public.debversion NOT NULL
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

CREATE SEQUENCE public.dist_cpe_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.dist_cpe_id_seq OWNER TO glvd;

--
-- Name: dist_cpe_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: glvd
--

ALTER SEQUENCE public.dist_cpe_id_seq OWNED BY public.dist_cpe.id;


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
-- Name: sourcepackagecve; Type: VIEW; Schema: public; Owner: glvd
--

CREATE VIEW public.sourcepackagecve AS
 SELECT all_cve.cve_id,
    deb_cve.deb_source AS source_package_name,
    deb_cve.deb_version AS source_package_version,
    dist_cpe.cpe_version AS gardenlinux_version,
    deb_cve.debsec_vulnerable AS is_vulnerable,
    (all_cve.data ->> 'published'::text) AS cve_published_date,
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
    (((((all_cve.data -> 'metrics'::text) -> 'cvssMetricV2'::text) -> 0) -> 'cvssData'::text) ->> 'vectorString'::text) AS vector_string_v2
   FROM ((public.all_cve
     JOIN public.deb_cve USING (cve_id))
     JOIN public.dist_cpe ON ((deb_cve.dist_id = dist_cpe.id)))
  WHERE ((dist_cpe.cpe_product = 'gardenlinux'::text) AND (deb_cve.debsec_vulnerable = true));


ALTER VIEW public.sourcepackagecve OWNER TO glvd;

--
-- Name: dist_cpe id; Type: DEFAULT; Schema: public; Owner: glvd
--

ALTER TABLE ONLY public.dist_cpe ALTER COLUMN id SET DEFAULT nextval('public.dist_cpe_id_seq'::regclass);


--
-- Name: all_cve all_cve_pkey; Type: CONSTRAINT; Schema: public; Owner: glvd
--

ALTER TABLE ONLY public.all_cve
    ADD CONSTRAINT all_cve_pkey PRIMARY KEY (cve_id);


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
-- Name: nvd_cve nvd_cve_pkey; Type: CONSTRAINT; Schema: public; Owner: glvd
--

ALTER TABLE ONLY public.nvd_cve
    ADD CONSTRAINT nvd_cve_pkey PRIMARY KEY (cve_id);


--
-- Name: deb_cve_search; Type: INDEX; Schema: public; Owner: glvd
--

CREATE INDEX deb_cve_search ON public.deb_cve USING btree (dist_id, debsec_vulnerable, deb_source, deb_version);


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
-- PostgreSQL database dump complete
--

