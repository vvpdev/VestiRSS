package com.vvp.vestirss.repository.network.xml_models

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(strict = false)
data class Channel (

    @field: ElementList(data = false, empty = true, inline = true, name = "item", required = false)
    var items: List<Item>? = null
)