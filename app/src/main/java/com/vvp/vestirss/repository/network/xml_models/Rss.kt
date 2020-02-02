package com.vvp.vestirss.repository.network.xml_models

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(strict = false)
data class Rss (

    @field: Element(name = "channel")
    var channel: Channel? = null
)



