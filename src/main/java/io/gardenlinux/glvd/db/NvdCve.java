package io.gardenlinux.glvd.db;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "nvd_cve")
public class NvdCve {
    @Id
    @Column(name = "cve_id", nullable = false)
    private String cveId;

    @Column(name = "last_mod", nullable = false)
    private String lastMod;

    @Convert(converter = NvdCveDataAttributeConverter.class)
    @Column(name = "data", nullable = false)
    private Data data;

    public NvdCve() {
    }

    public NvdCve(String cveId, String lastMod, Data data) {
        this.cveId = cveId;
        this.lastMod = lastMod;
        this.data = data;
    }

    public String getCveId() {
        return cveId;
    }

    public String getLastMod() {
        return lastMod;
    }

    public Data getData() {
        return data;
    }

    public record Data(
            String id,
            String sourceIdentifier,
            String published,
            String lastModified,
            String vulnStatus,
            List<String> cveTags,
            List<Description> descriptions,
            Metrics metrics,
            List<Weakness> weaknesses,
            List<Configuration> configurations,
            List<Reference> references
    ) {
        public record Description(
                String lang,
                String value
        ) {}

        public record Metrics(
                List<CvssMetricV40> cvssMetricV40,
                List<CvssMetricV31> cvssMetricV31,
                List<CvssMetricV30> cvssMetricV30,
                List<CvssMetricV2> cvssMetricV2
        ) {
            public record CvssMetricV40(
                    String source,
                    String type,
                    CvssData cvssData,
                    float exploitabilityScore,
                    float impactScore
            ) {
                public record CvssData(
                        String version,
                        String vectorString,
                        float baseScore,
                        String baseSeverity,
                        String attackVector,
                        String attackComplexity,
                        String privilegesRequired,
                        String userInteraction,
                        String scope,
                        String confidentialityImpact,
                        String integrityImpact,
                        String availabilityImpact
                ) {}
            }
            public record CvssMetricV31(
                    String source,
                    String type,
                    CvssData cvssData,
                    float exploitabilityScore,
                    float impactScore
            ) {
                public record CvssData(
                        String version,
                        String vectorString,
                        float baseScore,
                        String baseSeverity,
                        String attackVector,
                        String attackComplexity,
                        String privilegesRequired,
                        String userInteraction,
                        String scope,
                        String confidentialityImpact,
                        String integrityImpact,
                        String availabilityImpact
                ) {}
            }
            public record CvssMetricV30(
                    String source,
                    String type,
                    CvssData cvssData,
                    float exploitabilityScore,
                    float impactScore
            ) {
                public record CvssData(
                        String version,
                        String vectorString,
                        float baseScore,
                        String baseSeverity,
                        String attackVector,
                        String attackComplexity,
                        String privilegesRequired,
                        String userInteraction,
                        String scope,
                        String confidentialityImpact,
                        String integrityImpact,
                        String availabilityImpact
                ) {}
            }
            public record CvssMetricV2(
                    String source,
                    String type,
                    CvssData cvssData,
                    String baseSeverity,
                    float exploitabilityScore,
                    float impactScore,
                    boolean acInsufInfo,
                    boolean obtainAllPrivilege,
                    boolean obtainUserPrivilege,
                    boolean obtainOtherPrivilege,
                    boolean userInteractionRequired
            ) {
                public record CvssData(
                        String version,
                        String vectorString,
                        float baseScore,
                        String accessVector,
                        String accessComplexity,
                        String authentication,
                        String confidentialityImpact,
                        String integrityImpact,
                        String availabilityImpact
                ) {}
            }
        }

        public record Weakness(
                String source,
                String type,
                List<WeaknessDescription> description
        ) {
            public record WeaknessDescription(
                    String lang,
                    String value
            ) {}
        }

        public record Configuration(
                List<Node> nodes
        ) {
            public record Node(
                    String operator,
                    boolean negate,
                    List<CpeMatch> cpeMatch
            ) {
                public record CpeMatch(
                        boolean vulnerable,
                        String criteria,
                        String matchCriteriaId,
                        String versionEndExcluding,
                        String versionStartIncluding,
                        String versionEndIncluding
                ) {}
            }
        }

        public record Reference(
                String url,
                String source,
                List<String> tags
        ) {}
    }
}
