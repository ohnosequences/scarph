## Morphisms list


1. From/to unit

| `X <: AnyGraphObject` |    type    | syntax           |
|----------------------:|:----------:|:-----------------|
|         `fromUnit[X]` | `unit → X` | `.fromUnit(<x>)` |
|           `toUnit[X]` | `X → unit` | `.toUnit`        |


1. From/to zero

| `X <: AnyGraphObject` |    type    | syntax           |
|----------------------:|:----------:|:-----------------|
|         `fromZero[X]` | `zero → X` | `.fromZero(<x>)` |
|           `toZero[X]` | `X → zero` | `.toZero`        |


1. Tensor diagonal & condiagonal (`△` & `▽`)

| `X <: AnyGraphObject` |    type     | syntax       |
|----------------------:|:-----------:|:-------------|
|        `duplicate[X]` | `X → X ⊗ X` | `.duplicate` |
|          `matchUp[X]` | `X ⊗ X → X` | `.matchUp`   |


1. Biproduct diagonal & condiagonal

| `X <: AnyGraphObject` |    type     | syntax   |
|----------------------:|:-----------:|:---------|
|             `fork[X]` | `X → X ⊕ X` | `.fork`  |
|            `merge[X]` | `X ⊕ X → X` | `.merge` |


1. Biproduct left injection/projection

| `A, B <: AnyGraphObject` |    type     | syntax              |
|-------------------------:|:-----------:|:--------------------|
|          `leftInj[A, B]` | `A → A ⊕ B` | `.leftInj(<a ⊕ b>)` |
|         `leftProj[A, B]` | `A ⊕ B → A` | `.leftProj`         |


1. Biproduct right injection/projection

| `A, B <: AnyGraphObject` |    type     | syntax               |
|-------------------------:|:-----------:|:---------------------|
|         `rightInj[A, B]` | `B → A ⊕ B` | `.rightInj(<a ⊕ b>)` |
|        `rightProj[A, B]` | `A ⊕ B → B` | `.rightProj`         |


1. Edge target & vertex incoming edges

| `E <: AnyEdge` |      type      | syntax         |
|---------------:|:--------------:|:---------------|
|    `target[E]` | `E → E#Target` | `.target`      |
|       `inE[E]` | `E#Target → E` | `.inE(<edge>)` |


1. Edge source & vertex outgoing edges

| `E <: AnyEdge` |      type      | syntax          |
|---------------:|:--------------:|:----------------|
|    `source[E]` | `E → E#Source` | `.source`       |
|      `outE[E]` | `E#Source → E` | `.outE(<edge>)` |


1. Vertex incoming/outgoing edges

| `E <: AnyEdge` |         type          | syntax          |
|---------------:|:---------------------:|:----------------|
|      `outV[E]` | `E#Source → E#Target` | `.outV(<edge>)` |
|       `inV[E]` | `E#Target → E#Source` | `.inV(<edge>)`  |


1. Get element property & lookup element by property value

| `P <: AnyProperty` |        type         | syntax             |
|-------------------:|:-------------------:|:-------------------|
|           `get[P]` | `P#Owner → P#Value` | `.get(<property>)` |
|        `lookup[P]` | `P#Value → P#Owner` |                    |


1. Predicate quantification/coercion

| `P <: AnyPredicate` |      type       | syntax                   |
|--------------------:|:---------------:|:-------------------------|
|       `quantify[P]` | `P#Element → P` | `.quantify(<predicate>)` |
|         `coerce[P]` | `P → P#Element` | `.coerce`                |


#### Additional syntax

- `a.andThen(b) = a >=> b`
- `.filter(predicate) = .quantify(predicate).coerce`



### Isomorphisms list


|            isomorphism            |                   → | syntax   |                     ← | syntax         |
|:---------------------------------:|--------------------:|:---------|----------------------:|:---------------|
|              `X ≃ X`              |       `identity[X]` | --       |                    -- |                |
|          `A ⊗ B ≃ B ⊗ A`          |     `symmetry[A,B]` | `.twist` |                    -- |                |
| `U ⊗ (A ⊕ B) ≃ (U ⊗ A) ⊕ (U ⊗ B)` | `distribute[U,A,B]` | `TODO`   | `undistribute[U,A,B]` | `TODO`         |
|            `I ⊗ X ≃ X`            |       `leftUnit[X]` | `.right` |                    -- | `.leftCounit`  |
|            `X ⊗ I ≃ X`            |      `rightUnit[X]` | `.left`  |                    -- | `.rightCounit` |
|            `0 ⊕ X ≃ X`            |       `leftZero[X]` | `.right` |                    -- | `.leftCozero`  |
|            `X ⊕ 0 ≃ X`            |      `rightZero[X]` | `.left`  |                    -- | `.rightCozero` |
