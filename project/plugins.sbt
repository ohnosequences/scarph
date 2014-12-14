resolvers += "Era7 maven releases" at "https://releases.era7.com.s3.amazonaws.com"

resolvers += "Era7 maven snapshots" at "https://snapshots.era7.com.s3.amazonaws.com"

addSbtPlugin("ohnosequences" % "nice-sbt-settings" % "0.5.0-RC1")

// These versions fix the bug with unicode symbols:
// addSbtPlugin("laughedelic" % "literator-plugin" % "0.5.2")

// addSbtPlugin("com.markatta" % "taglist-plugin" % "1.3.1")
