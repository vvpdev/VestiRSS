package com.vvp.vestirss.repository.network.xml_models

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root


@Root(strict = false)
class Enclosure (

    @field: Attribute(name = "url")
    var url: String? = null

)