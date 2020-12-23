### 0.2.0

_Released 2020 Dec 23_

#### Improvements

- Endpoints:
    - Added support for `/v2/account/bank`. [[GH-3](https://github.com/GW2ToolBelt/api-generator/issues/3)]
    - Added support for `/v2/continents`. [[GH-70](https://github.com/GW2ToolBelt/api-generator/issues/70)]
    - Added support for `/v2/emblem`. [[GH-76](https://github.com/GW2ToolBelt/api-generator/issues/76)]
    - Added support for `/v2/emblem/:type`. [[GH-76](https://github.com/GW2ToolBelt/api-generator/issues/76)]
    - Added support for `/v2/gliders`. [[GH-80](https://github.com/GW2ToolBelt/api-generator/issues/80)]
    - Added support for `/v2/guild/:id`. [[GH-81](https://github.com/GW2ToolBelt/api-generator/issues/81)]
    - Added support for `/v2/maps`. [[GH-101](https://github.com/GW2ToolBelt/api-generator/issues/101)]
    - Added support for `/v2/masteries`. [[GH-102](https://github.com/GW2ToolBelt/api-generator/issues/102)]
    - Added support for `/v2/materials`. [[GH-103](https://github.com/GW2ToolBelt/api-generator/issues/103)]
    - Added support for `/v2/recipes`. [[GH-126](https://github.com/GW2ToolBelt/api-generator/issues/126)]
- Added support for V2 schema `2020-11-17T00:30:00.000Z`.
- Path-parameters and query-parameters now have a `camelCaseName` property.

#### Fixes

- Endpoints:
    - `isLocalized` flag is now set for `/v2/items`.


---

### 0.1.0

_Released 2020 Sep 30_

#### Overview

A library for programmatically creating programs that interface with data exposed
by the official Guild Wars 2 API.

This library contains information about the structure and data-types of the API.
This information can be used to generate custom API clients or other programs
which require definitions of the API.