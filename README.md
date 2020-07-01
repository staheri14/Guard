# Guard
Guard is a secure routing protocol for skip-graphs. It makes use of digital signature schemes and supervisor nodes called *guards* to assert the correctness during the lookup operations.
This particular implementation makes use of [jPBC library](#) and the open-source SkipGraph implementation provided [here](#).
## Layers
This implementation makes use of a layered architecture. From bottom to top, the layers for an authenticated node are as following:
1. **Communication layer:** Handles node-to-node communication.
2. **Authentication layer:** Implements the phases of Guard. Inserts & verifies the authentication meta-data related to authenticated search queries.
3. **Skip node layer:** Implements a skip-graph DHT node. Supports `search` and `join` operations.
4. **User interface:** Coordinates the authenticated/unauthenticated `search`, `join` operations. Per-node experiments are also handled by this layer.

Guard also makes use of a trusted third party (TTP) during the registration, construction and guard assignment phases. TTP also acts as the experiment controller. It is implemented as a separate application, and its layers are as following:
1. **Communication layer:** Handles TTP-to-node and node-to-TTP communication.
2. **TTP layer:** Contains the trusted third party protocols (e.g. registration) specific to Guard.
3. **TTP user interface:** Used to coordinate the experiment from a central point.

Each layer is implemented by extending the `Layer` abstract class. The specific layer implementations reside in their own packages.

## System parameters
The system parameters are defined under the `ttp` package, in `SystemParameters.java` file. System parameters are initialized at the TTP, and at the registration phase, they are transferred to the nodes. Here are the explanations of the parameters:

## Running
To run Guard, 



## Node operations

## TTP operations

## Taking measurements