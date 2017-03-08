## Morphisms list

### Basic ones

#### From/to unit

| `X <: AnyGraphObject` |    type    | syntax         |
|----------------------:|:----------:|:---------------|
|         `fromUnit[X]` | `unit → X` | `.fromUnit(x)` |
|           `toUnit[X]` | `X → unit` | `.toUnit`      |


#### From/to zero

| `X <: AnyGraphObject` |    type    | syntax         |
|----------------------:|:----------:|:---------------|
|         `fromZero[X]` | `zero → X` | `.fromZero(x)` |
|           `toZero[X]` | `X → zero` | `.toZero`      |


#### Tensor diagonal & codiagonal (`△` & `▽`)

| `X <: AnyGraphObject` |    type     | syntax       |
|----------------------:|:-----------:|:-------------|
|        `duplicate[X]` | `X → X ⊗ X` | `.duplicate` |
|          `matchUp[X]` | `X ⊗ X → X` | `.matchUp`   |


#### Biproduct diagonal & codiagonal

| `X <: AnyGraphObject` |    type     | syntax   |
|----------------------:|:-----------:|:---------|
|             `fork[X]` | `X → X ⊕ X` | `.fork`  |
|            `merge[X]` | `X ⊕ X → X` | `.merge` |


#### Biproduct left injection/projection

| `A, B <: AnyGraphObject` |    type     | syntax            |
|-------------------------:|:-----------:|:------------------|
|          `leftInj[A, B]` | `A → A ⊕ B` | `.leftInj(a ⊕ b)` |
|         `leftProj[A, B]` | `A ⊕ B → A` | `.leftProj`       |


#### Biproduct right injection/projection

| `A, B <: AnyGraphObject` |    type     | syntax             |
|-------------------------:|:-----------:|:-------------------|
|         `rightInj[A, B]` | `B → A ⊕ B` | `.rightInj(a ⊕ b)` |
|        `rightProj[A, B]` | `A ⊕ B → B` | `.rightProj`       |


#### Edge target & vertex incoming edges

| `E <: AnyEdge` |      type      | syntax    |
|---------------:|:--------------:|:----------|
|    `target[E]` | `E → E#Target` | `.target` |
|       `inE[E]` | `E#Target → E` | `.inE(e)` |


#### Edge source & vertex outgoing edges

| `E <: AnyEdge` |      type      | syntax     |
|---------------:|:--------------:|:-----------|
|    `source[E]` | `E → E#Source` | `.source`  |
|      `outE[E]` | `E#Source → E` | `.outE(e)` |


#### Vertex incoming/outgoing edges

| `E <: AnyEdge` |         type          | syntax     |
|---------------:|:---------------------:|:-----------|
|      `outV[E]` | `E#Source → E#Target` | `.outV(e)` |
|       `inV[E]` | `E#Target → E#Source` | `.inV(e)`  |


#### Get element property & lookup element by property value

| `P <: AnyProperty` |        type         | syntax    |
|-------------------:|:-------------------:|:----------|
|           `get[P]` | `P#Owner → P#Value` | `.get(p)` |
|        `lookup[P]` | `P#Value → P#Owner` |           |


#### Predicate quantification/coercion

| `P <: AnyPredicate` |      type       | syntax                   |
|--------------------:|:---------------:|:-------------------------|
|       `quantify[P]` | `P#Element → P` | `.quantify(<predicate>)` |
|         `coerce[P]` | `P → P#Element` | `.coerce`                |


### Isomorphisms

#### Identity

| isomorphism |             → | syntax | ← | syntax |
|:-----------:|--------------:|:-------|--:|:-------|
|    X ≃ X    | `identity[X]` |        |   |        |

#### Symmetry

|  isomorphism  |               → | syntax   | ← | syntax |
|:-------------:|----------------:|:---------|--:|:-------|
| A ⊗ B ≃ B ⊗ A | `symmetry[A,B]` | `.twist` |   |        |

#### Distributivity

|           isomorphism           |                   → | syntax        |                     ← | syntax          |
|:-------------------------------:|--------------------:|:--------------|----------------------:|:----------------|
| U ⊗ (A ⊕ B) ≃ (U ⊗ A) ⊕ (U ⊗ B) | `distribute[U,A,B]` | `.distribute` | `undistribute[U,A,B]` | `.undistribute` |

#### Associativity of ⊗ and ⊕

|        isomorphism        |                        → | syntax           |                         ← | syntax            |
|:-------------------------:|-------------------------:|:-----------------|--------------------------:|:------------------|
| A ⊕ (B ⊕ C) ≃ (A ⊕ B) ⊕ C | `associateBiproductLeft` | `.associateLeft` | `associateBiproductRight` | `.associateRight` |
| A ⊗ (B ⊗ C) ≃ (A ⊗ B) ⊗ C |    `associateTensorLeft` | `.associateLeft` |    `associateTensorRight` | `.associateRight` |

#### Zero with ⊕

| isomorphism |              → | syntax       |                ← | syntax         |
|:-----------:|---------------:|:-------------|-----------------:|:---------------|
|  0 ⊕ X ≃ X  |  `leftZero[X]` | `.leftZero`  |  `leftCozero[X]` | `.leftCozero`  |
|  X ⊕ 0 ≃ X  | `rightZero[X]` | `.rightZero` | `rightCozero[X]` | `.rightCozero` |

#### Unit with ⊗

| isomorphism |              → | syntax       |                ← | syntax         |
|:-----------:|---------------:|:-------------|-----------------:|:---------------|
|  1 ⊗ X ≃ X  |  `leftUnit[X]` | `.leftUnit`  |  `leftCounit[X]` | `.leftCounit`  |
|  X ⊗ 1 ≃ X  | `rightUnit[X]` | `.rightUnit` | `rightCounit[X]` | `.rightCounit` |

#### ~~Zero with ⊗~~

| isomorphism |                   → | syntax |                     ← | syntax |
|:-----------:|--------------------:|:-------|----------------------:|:-------|
|  X ⊗ 0 ≃ 0  | `timesRightZero[X]` |        | `timesRightCozero[X]` |        |
|  0 ⊗ X ≃ 0  |  `timesLeftZero[X]` |        |  `timesLeftCozero[X]` |        |


### Additional syntax

- `a.andThen(b)` = `a >=> b`
- `.filter(predicate)` = `.quantify(predicate).coerce`
