package com.cangjiedata.quilljs.bean

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * Create by Judge at 2/6/21
 */

const val QUILL_TYPE_UNDEFINE = 0
const val QUILL_TYPE_TEXT = -1
const val QUILL_TYPE_IMAGE = -2
const val QUILL_TYPE_VIDEO = -3

open class QuillJsParamBean(override val itemType: Int) : MultiItemEntity {
    var entries : ArrayList<QuillJsBean> = ArrayList()
}

class QuillJsBean(var insert: String) {
    var attributes: Attributes? = null
    fun reloadAttributes(attr: Attributes) {
        if (this.attributes == null) {
            this.attributes = attr
        } else {
            attr.link?.let {
                this.attributes!!.link = it
            }
            attr.bold?.let {
                this.attributes!!.bold = it
            }
            attr.italic?.let {
                this.attributes!!.italic = it
            }
            attr.header?.let {
                this.attributes!!.header = it
            }
            attr.align?.let {
                this.attributes!!.align = it
            }
            attr.background?.let {
                this.attributes!!.background = it
            }
            attr.color?.let {
                this.attributes!!.color = it
            }
            attr.size?.let {
                this.attributes!!.size = it
            }
        }
    }
}

class Attributes {
    var link: String? = null
    var italic: Boolean? = null
    var bold: Boolean? = null
    var header: String? = null
    var align: String? = null
    var background: String? = null
    var color: String? = null
    var size: String? = null
}