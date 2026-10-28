# ExcellentCrates Folia 26.2 — experimental port

This GPL-3.0 fork is derived from
https://github.com/FluffyworldMC/ExcellentCrates-v7-folia
at commit dfa60d3b412c2dec66f738f198696076fe402baf, which itself is a
community adaptation of NightExpress ExcellentCrates. Original copyright and
license are retained; see LICENSE.

**Release candidate version: 7.1.0-folia.26.2.1.**
This identifies the **7.1.0** source base, compiled with the **26.2** Paper API.
It is **NOT** official ExcellentCrates 7.2.0, and does not include the 7.2.0
bug fixes or developer API features.

The original v7 Folia adapter uses UniversalScheduler 0.1.7.
The plugin.yml / paper-plugin.yml declare folia-supported: true.
That metadata permits plugin loading but **does not establish thread safety**.
Player and entity operations require entity scheduler ownership; block/world
operations require the correct region scheduler and global state access must be
safe. See parent tracker:
https://github.com/CyoriaSMP-Team/Folia-Ports/issues/5

## How to build

GitHub Actions .github/workflows/build-folia-26-2.yml uses Temurin Java 25
and Gradle 9.2.1 to build the shadowJar plugin JAR. The main source difference
from the source baseline is targeting io.papermc.paper:paper-api:26.2.build.+.

## Tested

- [ ] GitHub Actions Java 25 build passes
- [ ] Startup on an isolated Folia 26.2 server with compatible NightCore
- [ ] Concurrent opening, costs/key deductions, reward distribution and rollback
- [ ] GUI editing, animations, holograms, world region transfer
- [ ] 7.2.0 source parity / upstream fixes

**Do not deploy to production until the relevant runtime tests pass.
No server modifications are performed by this project.**
