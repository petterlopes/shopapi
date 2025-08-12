Param(
    [Parameter(Mandatory=$true)]
    [string]$ProjectPath
)
$ErrorActionPreference = "Stop"

$wrapperDir = Join-Path $ProjectPath ".mvn\wrapper"
if (!(Test-Path $wrapperDir)) {
    New-Item -ItemType Directory -Force -Path $wrapperDir | Out-Null
}

$propsPath = Join-Path $wrapperDir "maven-wrapper.properties"
@"
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip
wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar
"@ | Set-Content -Encoding UTF8 $propsPath

Write-Host "Atualizado: $propsPath" -ForegroundColor Green
Write-Host "Agora execute:" -ForegroundColor Cyan
Write-Host "  .\mvnw.cmd -v"
