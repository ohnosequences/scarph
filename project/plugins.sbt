resolvers += "Era7 maven releases" at "https://s3.amazonaws.com/releases.era7.com"

resolvers += "Era7 maven snapshots" at "https://s3.amazonaws.com/snapshots.era7.com"

addSbtPlugin("ohnosequences" % "nice-sbt-settings" % "0.5.0-RC1")

// These versions fix the bug with unicode symbols:
// addSbtPlugin("laughedelic" % "literator-plugin" % "0.5.2")

// addSbtPlugin("com.markatta" % "taglist-plugin" % "1.3.1")
