## Morphisms list

Assuming

```scala
X, A, B, U <: AnyGraphObject
E <: AnyEdge
P <: AnyProperty
```

|        morphism |        meaning        | syntax             |           dagger | meaning               | syntax          |
|----------------:|:---------------------:|:-------------------|-----------------:|:----------------------|:----------------|
|   `fromUnit[X]` |        `I → X`        | --                 |      `toUnit[X]` | `X → I`               | `.toUnit`       |
|   `fromZero[X]` |        `0 → X`        | --                 |      `toZero[X]` | `X → 0`               | `.toZero`       |
|  `duplicate[X]` |      `X → X ⊗ X`      | `.duplicate`       |     `matchUp[X]` | `X ⊗ X → X`           | `TODO`          |
|      `split[X]` |      `X → X ⊕ X`      | `.split`           |       `merge[X]` | `X ⊕ X → X`           | `TODO`          |
|  `leftInj[A,B]` |      `A → A ⊕ B`      | --                 |  `leftProj[A,B]` | `A ⊕ B → A`           | `.left`         |
| `rightInj[A,B]` |      `B → A ⊕ B`      | --                 | `rightProj[A,B]` | `A ⊕ B → B`           | `.right`        |
|     `target[E]` |    `E → E#Target`     | `.target`          |         `inE[E]` | `E#Target → E`        | `.inE(<edge>)`  |
|     `source[E]` |    `E → E#Source`     | `.source`          |        `outE[E]` | `E#Source → E`        | `.outE(<edge>)` |
|       `outV[E]` | `E#Source → E#Target` | `.outV(<edge>)`    |         `inV[E]` | `E#Target → E#Source` | `.inV(<edge>)`  |
|        `get[P]` |  `P#Owner → P#Value`  | `.get(<property>)` |      `lookup[P]` | `P#Value → P#Owner`   | --              |

Here `TODO` means that it will be done in the nearest future, and `--` means that it is not needed.


### Isomorphisms list


|            isomorphism            |                   → | syntax   |                     ← | syntax         |
|:---------------------------------:|--------------------:|:---------|----------------------:|:---------------|
|              `X ≃ X`              |       `identity[X]` | --       |                    -- |                |
|          `A ⊗ B ≃ B ⊗ A`          |     `symmetry[A,B]` | `.twist` |                    -- |                |
| `U ⊗ (A ⊕ B) ≃ (U ⊗ A) ⊕ (U ⊗ B)` | `distribute[U,A,B]` | `TODO`   | `undistribute[U,A,B]` | `TODO`         |
|            `I ⊗ X ≃ X`            |       `leftUnit[X]` | `.right` |       `leftCounit[X]` | `.leftCounit`  |
|            `X ⊗ I ≃ X`            |      `rightUnit[X]` | `.left`  |      `rightCounit[X]` | `.rightCounit` |
|            `0 ⊕ X ≃ X`            |       `leftZero[X]` | `.right` |       `leftCozero[X]` | `.leftCozero`  |
|            `X ⊕ 0 ≃ X`            |      `rightZero[X]` | `.left`  |      `rightCozero[X]` | `.rightCozero` |
