@echo off
for /r ..\..\target\jaxws\wsimport\java %%X in (*.java) do (call copylic2 %%X)