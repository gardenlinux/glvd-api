# Get CVEs by Garden Linux Version
GET http://localhost:8080/v1/cves/1592.4

# Get CVEs by Garden Linux Version Packages
GET http://localhost:8080/v1/cves/1592.4/packages/vim,bash,python3,curl,jinja2

# Get Packages by Vulnerability
GET http://localhost:8080/v1/distro/1592.4/CVE-2024-56326

# Get Vulnerabilities by Package by Version
GET http://localhost:8080/v1/packages/jinja2/3.1.3-1

# Get Vulnerabilities by Package
GET http://localhost:8080/v1/packages/jinja2

# Get CVEs by Gardenlinux Version Packages PUT
PUT http://localhost:8080/v1/cves/1592.4/packages
{
  "packageNames": [
    "vim",
    "bash",
    "python3",
    "curl",
    "jinja2"
  ]
}
