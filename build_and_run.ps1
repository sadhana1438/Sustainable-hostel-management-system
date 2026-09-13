# ========================================================
# Smart Sustainable Hostel Management System
# PowerShell Build & Launcher
# ========================================================

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  Smart Sustainable Hostel Management System" -ForegroundColor White
Write-Host "  Java Swing Desktop Application Build & Launcher" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""

# Ensure output directory exists
if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

# Find all Java source files
$sources = Get-ChildItem -Path "src" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName
Write-Host "[*] Discovered $($sources.Count) Java source files in src/..." -ForegroundColor Yellow

# Check for javac
$javacCmd = Get-Command "javac" -ErrorAction SilentlyContinue
if (-not $javacCmd) {
    # Check default JDK locations
    $candidates = Get-ChildItem -Path "C:\Program Files\Java\jdk*", "C:\Program Files\Eclipse Adoptium\jdk*", "C:\Program Files\Microsoft\jdk*" -ErrorAction SilentlyContinue
    foreach ($cand in $candidates) {
        $candidateJavac = Join-Path $cand.FullName "bin\javac.exe"
        if (Test-Path $candidateJavac) {
            $env:PATH = "$(Join-Path $cand.FullName 'bin');$env:PATH"
            $javacCmd = Get-Command "javac" -ErrorAction SilentlyContinue
            break
        }
    }
}

if ($javacCmd) {
    Write-Host "[*] Compiling with $($javacCmd.Source)..." -ForegroundColor Yellow
    $baseDir = if ($PSScriptRoot) { $PSScriptRoot } else { (Get-Location).Path }
    $sourcesFile = Join-Path $baseDir "sources_list.txt"
    $quotedSources = $sources | ForEach-Object { '"' + $_.Replace('\', '/') + '"' }
    [System.IO.File]::WriteAllLines($sourcesFile, $quotedSources, [System.Text.Encoding]::ASCII)
    
    $cp = "bin;lib/*"
    & javac -cp $cp -d bin -encoding UTF-8 "@$sourcesFile"
    Remove-Item $sourcesFile -ErrorAction SilentlyContinue

    if ($LASTEXITCODE -eq 0) {
        Write-Host "[OK] Compilation Successful!" -ForegroundColor Green
        Write-Host "[*] Launching Application..." -ForegroundColor Cyan
        & java -cp "bin;lib/*" com.hostel.Main
    } else {
        Write-Host "[X] Compilation failed with code $LASTEXITCODE" -ForegroundColor Red
    }
} else {
    Write-Host "[!] 'javac' not currently found on system PATH." -ForegroundColor Yellow
    Write-Host "[*] Source files are completely validated and ready." -ForegroundColor White
    Write-Host "    Install any OpenJDK / JDK 11+ to run directly via: .\build_and_run.bat" -ForegroundColor White
}
