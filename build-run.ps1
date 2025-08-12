Param(
    [string]$Profile = "local",
    [switch]$Wrapper
)
$ErrorActionPreference = "Stop"

Write-Host "==> Buildando..." -ForegroundColor Cyan
if ($Wrapper) {
  if (!(Test-Path ".\mvnw.cmd")) { & mvn -N wrapper:wrapper -Dmaven=3.9.9 -Dtype=bin }
  & .\mvnw.cmd -q clean package -DskipTests
} else {
  & mvn -q clean package -DskipTests
}
if ($LASTEXITCODE -ne 0) { throw "Build falhou" }

$jar = Get-ChildItem ".\target\shopapi-*.jar" | Where-Object { $_.Name -notmatch "original" } | Sort-Object LastWriteTime -Descending | Select-Object -First 1
if (-not $jar) { throw "JAR não encontrado" }

Write-Host "==> Executando..." -ForegroundColor Cyan
& java -jar $jar.FullName --spring.profiles.active=$Profile
