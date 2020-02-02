package com.vvp.vestirss.repository.network.xml_models

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root

@Root(strict = false)
data class Item (

    @field: Element(name = "title")
    var title:String? = null,

    @field: Element(name = "description")
    var description: String? = null,

    @field: Element(name = "pubDate")
    var pubDate: String? = null,

    @field: Element(name = "category")
    var category:String? = null,

    @field: ElementList(data = false, empty = true, inline = true, name = "enclosure", required = false)
    var enclosure : List<Enclosure>? = null,

    @field: Namespace(reference = "yandex")
    @field: Element(data = false, name = "full-text", required = false)
    var yandexFullText: String? = null

)