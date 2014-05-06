resolvers += "Era7 maven releases" at "http://releases.era7.com.s3.amazonaws.com"

addSbtPlugin("ohnosequences" % "nice-sbt-settings" % "0.4.0")

// This version fixes the bug with unicode symbols:
addSbtPlugin("laughedelic" % "literator-plugin" % "0.5.2")
