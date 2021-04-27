package com.cangjiedata.quilljs.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.util.Base64
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.cangjiedata.baselibrary.utils.dip2px
import com.cangjiedata.baselibrary.utils.loge
import com.cangjiedata.quilljs.R
import com.cangjiedata.quilljs.bean.*
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.Gson
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * Create by Judge at 2/6/21
 */
abstract class QuillJsAdapter<T : QuillJsParamBean> : BaseMultiItemQuickAdapter<T, BaseViewHolder>() {
    init {
        addItemType(QUILL_TYPE_UNDEFINE, R.layout.item_undefine)
        addItemType(QUILL_TYPE_IMAGE, R.layout.quill_item_image)
        addItemType(QUILL_TYPE_VIDEO, R.layout.quill_item_video)
        addItemType(QUILL_TYPE_TEXT, R.layout.quill_item_text)
        this.addOtherItemType()
    }
    var onQuillJsEndListener:OnQuillJsEndListener? = null

    open interface OnQuillJsEndListener{
        fun onQuillJsEn()
    }

    fun appendQuillJs(quillJsString: String){
        GlobalScope.launch {
            try {
                val data = JSONArray(quillJsString)
                val paramsData = ArrayList<QuillJsParamBean>()
                val entries: ArrayList<QuillJsBean> = ArrayList()
                for (item in 0 until data.length()) {
                    val child = data.optJSONObject(item).optJSONObject("insert")
                    if (child == null) {//非对象则为文本
                        var text = data.optJSONObject(item).optString("insert")
                        if (text.contains("\n")) {//包含换行，进行切割
                            if (TextUtils.equals(text, "\n")) {
                                if (entries.isEmpty()) {
                                    if(paramsData.isEmpty()){
                                        paramsData.add(QuillJsParamBean(QUILL_TYPE_TEXT))
                                    }
                                    paramsData[paramsData.lastIndex].entries.forEach {
                                        it.reloadAttributes(paramAttributes(data.optJSONObject(item)))
                                    }
                                } else {
                                    entries.forEach {
                                        it.reloadAttributes(paramAttributes(data.optJSONObject(item)))
                                    }
                                    paramsData.add(QuillJsParamBean(QUILL_TYPE_TEXT).apply {
                                        this.entries = ArrayList(entries)
                                    })
                                    entries.clear()
                                }
                            } else {
                                if (text.startsWith("\n")) {
                                    paramsData.add(QuillJsParamBean(QUILL_TYPE_TEXT).apply {
                                        this.entries = ArrayList(entries)
                                    })
                                    entries.clear()
                                }
                                if (text.endsWith("\n")) {
                                    text = text.substring(0, text.lastIndexOf("\n"))
                                }
                                val splits = text.split("\n")
                                for (index in splits.indices) {
                                    entries.add(QuillJsBean(splits[index]).apply {
                                        this.reloadAttributes(paramAttributes(data.optJSONObject(item)))
                                    })
                                    paramsData.add(QuillJsParamBean(QUILL_TYPE_TEXT).apply {
                                        this.entries = ArrayList(entries)
                                    })
                                    entries.clear()
                                }
                            }
                        } else {//不包含换行，直接设置到片段缓存
                            entries.add(QuillJsBean(text).apply {
                                reloadAttributes(paramAttributes(data.optJSONObject(item)))
                            })
                        }
                    } else {//对象单独换行
                        when {
                            child.optString("image").isNotEmpty() -> {
                                entries.add(QuillJsBean(child.optString("image")).apply {
                                    reloadAttributes(paramAttributes(data.optJSONObject(item)))
                                })
                                paramsData.add(QuillJsParamBean(QUILL_TYPE_IMAGE).apply {
                                    this.entries = ArrayList(entries)
                                })
                                entries.clear()
                            }
                            child.optString("video").isNotEmpty() -> {
                                entries.add(QuillJsBean(child.optString("video")).apply {
                                    reloadAttributes(paramAttributes(data.optJSONObject(item)))
                                })
                                paramsData.add(QuillJsParamBean(QUILL_TYPE_VIDEO).apply {
                                    this.entries = ArrayList(entries)
                                })
                                entries.clear()
                            }
                            else -> {
                                entries.add(QuillJsBean(child.toString()).apply {
                                    reloadAttributes(paramAttributes(data.optJSONObject(item)))
                                })
                                paramsData.add(QuillJsParamBean(QUILL_TYPE_UNDEFINE).apply {
                                    this.entries = ArrayList(entries)
                                })
                                entries.clear()
                            }
                        }
                    }
                }
                withContext(Dispatchers.Main){
                    appendQuillJsContent(paramsData)
                    onQuillJsEndListener?.onQuillJsEn()
                    loge("news detail lines ${paramsData.size}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onQuillJsEndListener?.onQuillJsEn()
            }
        }
    }

    abstract fun addOtherItemType()

    abstract fun appendQuillJsContent(list: ArrayList<QuillJsParamBean>)

    private fun paramAttributes(attr: JSONObject): Attributes {
        return Gson().fromJson(attr.optString("attributes", "{}"), Attributes::class.java)
    }

    override fun convert(holder: BaseViewHolder, item: T) {
        when (item.itemType) {
            QUILL_TYPE_IMAGE -> {
                item.entries[0].insert.let {
                    if (it.startsWith("data:")) {//base64图片格式
                        val decode: ByteArray = Base64.decode(it.split(",")[1], Base64.DEFAULT)
                        val bitmap: Bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.size)
                        holder.setImageBitmap(R.id.ivImage, bitmap)
                    } else {
                        Glide.with(context).load(it).diskCacheStrategy(DiskCacheStrategy.ALL) //缓存全尺寸
                            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .format(DecodeFormat.PREFER_RGB_565)
                            .error(R.mipmap.pic_loading_9).placeholder(R.mipmap.pic_loading_9)
                            .into(holder.getView(R.id.ivImage))
                    }
                }
            }

            QUILL_TYPE_VIDEO -> {

            }

            QUILL_TYPE_TEXT -> {
                val spanStrBuilder = SpannableStringBuilder()
                val textView = holder.getView<TextView>(R.id.tvContent)
                for (index in item.entries.indices) {
                    val data = item.entries[index]
                    val spanStr = SpannableString(data.insert)
                    data.attributes?.let {

                        when (it.align) {
                            "right" -> {
                                textView.gravity = Gravity.END
                            }
                            "center" -> {
                                textView.gravity = Gravity.CENTER_HORIZONTAL
                            }
                            else -> {
                                textView.gravity = Gravity.START
                            }
                        }

                        if (it.background.isNullOrEmpty()) {
                            textView.setBackgroundColor(context.resources.getColor(android.R.color.transparent))
                        } else {
                            textView.setBackgroundColor(Color.parseColor(it.background))
                        }

                        when (it.header) {
                            "1" -> {
                                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                                textView.setTypeface(textView.typeface, Typeface.BOLD)
                                if (it.italic == true) {
                                    spanStr.setSpan(StyleSpan(Typeface.ITALIC), 0, spanStr.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                                } else {
                                    spanStr.setSpan(StyleSpan(Typeface.NORMAL), 0, spanStr.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                                }
                                if (!it.link.isNullOrEmpty()) {
                                    val urlSpan = URLSpan(it.link)
                                    spanStr.setSpan(urlSpan, 0, spanStr.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                                    textView.movementMethod = LinkMovementMethod.getInstance()
                                }
                            }

                            "2" -> {
                                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                                textView.setTypeface(textView.typeface, Typeface.BOLD)
                                if (it.italic == true) {
                                    spanStr.setSpan(StyleSpan(Typeface.ITALIC), 0, spanStr.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                                } else {
                                    spanStr.setSpan(StyleSpan(Typeface.NORMAL), 0, spanStr.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                                }
                                if (!it.link.isNullOrEmpty()) {
                                    val urlSpan = URLSpan(it.link)
                                    spanStr.setSpan(urlSpan, 0, spanStr.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                                    textView.movementMethod = LinkMovementMethod.getInstance()
                                }
                            }

                            else -> {
                                if (it.color.isNullOrEmpty()) {
                                    val colorSpan = ForegroundColorSpan(context.resources.getColor(R.color.color_333333))
                                    spanStr.setSpan(colorSpan, 0, spanStr.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                                } else {
                                    val colorSpan = ForegroundColorSpan(Color.parseColor(it.color))
                                    spanStr.setSpan(colorSpan, 0, spanStr.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                                }
                                if (it.size.isNullOrEmpty()) {
                                    spanStr.setSpan(RelativeSizeSpan(1f), 0, spanStr.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                                } else {
                                    spanStr.setSpan(RelativeSizeSpan(it.size!!.toFloat() / 14f), 0, spanStr.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                                }

                                if (it.bold == true || it.italic == true) {
                                    if (it.bold == true && it.italic == true) {
                                        spanStr.setSpan(StyleSpan(Typeface.BOLD_ITALIC), 0, spanStr.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                                    } else if (it.bold == true) {
                                        spanStr.setSpan(StyleSpan(Typeface.BOLD), 0, spanStr.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                                    } else {
                                        spanStr.setSpan(StyleSpan(Typeface.ITALIC), 0, spanStr.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                                    }
                                } else {
                                    spanStr.setSpan(StyleSpan(Typeface.NORMAL), 0, spanStr.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                                }

                                if (!it.link.isNullOrEmpty()) {
                                    val urlSpan = URLSpan(it.link)
                                    spanStr.setSpan(urlSpan, 0, spanStr.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                                    textView.movementMethod = LinkMovementMethod.getInstance()
                                }
                            }
                        }
                    }
                    spanStrBuilder.append(spanStr)
                }
                holder.setText(R.id.tvContent, spanStrBuilder)
//                val spanStrBuilder = SpannableStringBuilder()
//                for (index in 0 until item.child.size) {
//                    val spanStr = SpannableString(item.child[index].insert
//                    spanStrBuilder.append(spanStr)
//                }
//                holder.setText(R.id.tvContent, spanStrBuilder)
            }
            QUILL_TYPE_UNDEFINE -> {

            }

            else ->{
                convertOther(holder, item)
            }
        }
    }

    abstract fun convertOther(holder: BaseViewHolder, item: T)
}