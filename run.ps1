# Usa Java 21 neste projeto (ignora JAVA_HOME global do Windows)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.10"
$env:Path = "$env:JAVA_HOME\bin;" + ($env:Path -replace [regex]::Escape("$env:JAVA_HOME\bin;"), "")

& "$PSScriptRoot\mvnw.cmd" @args
