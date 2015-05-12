package models

import sorm._

object DB extends Instance(entities = Seq(Entity[Sentence](), Entity[Sentiment]()), url = "jdbc:h2:mem:test")