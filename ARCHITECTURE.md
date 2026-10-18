# System Architecture — Android Survey Platform

This document details the Android activity lifecycle, survey dynamic adapter hierarchy, and Firebase Realtime Database schema.

## Architecture Overview

```text
[ Android Activity / Fragment ]
         |
         +---> [ Category Recycler Adapter ] (Gaming, Tech, Campus, Sports)
         |
         +---> [ Dynamic Questionnaire View Engine ]
         |            |
         |            +--- RadioGroup / Checkbox Input Clamping
         |            +--- Form Validation Rules
         |
         +---> [ Firebase Realtime Database Client ]
                   |
                   +--- /surveys/{categoryId}/submissions/{submissionId}
```

### Mobile Optimization Principles
1. **Memory Safety:** View references detach in `onDestroyView` to prevent Android Activity memory leaks.
2. **Offline Queuing:** Firebase offline persistence caches pending responses until network connectivity resumes.
