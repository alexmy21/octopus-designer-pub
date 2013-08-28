rem Guess OCTOPUS_HOME if not defined

set CURRENT_DIR=%cd%

if not "%OCTOPUS_HOME%" == "" goto gotHome
set OCTOPUS_HOME=%CURRENT_DIR%
if exist "%OCTOPUS_HOME%\bin\start.bat" goto okHome
cd ..
set OCTOPUS_HOME=%cd%
cd %CURRENT_DIR%
:gotHome
if exist "%OCTOPUS_HOME%\bin\start.bat" goto okHome
echo The OCTOPUS_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end
:okHome


java -classpath "%OCTOPUS_HOME%\lib\*" org.lisapark.octopus.designer.DesignerApplication "%OCTOPUS_HOME%\conf\octopus.properties"

:end