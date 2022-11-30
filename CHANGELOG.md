
<!--
Copyright (c) 2022 All project authors and nebula-contrib. All rights reserved.

This source code is licensed under Apache 2.0 License.
-->

# 1.1.0
## Features
- springcloud+nacos support #55 
- add upsert tag/edge function #82 
- support #39, use @javax.persistence.Transient #43

## Enhancement
- enhanced: #64 `debug log` print current space in session before switch #79 
- enhanced: NebulaDaoBasic default impls can be overwritten by xml #76 
- optimize #69 display exception detail & enable NebulaDaoBasic to support space switching #70 
- docs typo #52 

## Bugfix
- fixed #89 Set serialization to NebulaValue #97
- fixed #89 BigDecimal serialization to NebulaValue #97
- fixed #89 splitting param serialization into two forms, json and NebulaValue #92 
- fixed #78 use space and gql are executed together incorrect in 3.3.0 #87 
- fixed #73 `selectById` use id value embedding instead of cypher parameter #74 
- fixed #65 `selectByIds` use id values embedding instead of cypher param #67 
- fixed the error of "ng.id" when id is in super class #62 
- fixed #51 The node params support the direct use of the ID value when insert edge #60 
- fixed #56 make it work well when returnType is Map and result is null #58 
- fixed #47 console bug when result type is basic type #48 

# 1.1.0-rc

# 1.1.0-beta
