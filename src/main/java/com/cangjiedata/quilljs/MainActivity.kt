package com.cangjiedata.quilljs

import android.os.Bundle
import com.cangjiedata.baselibrary.utils.initLinearRecyclerView
import com.cangjiedata.baselibrary.view.BaseActivity
import com.cangjiedata.quilljs.adapter.QuillJsAdapter
import com.cangjiedata.quilljs.bean.QuillJsParamBean
import com.cangjiedata.quilljs.databinding.ActivityMainBinding
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class MainActivity : BaseActivity<ActivityMainBinding>(){
    override fun initViews(savedInstanceState: Bundle?) {
        val content =
            "[{\n" + "  \"insert\": \"居左1\"\n" + "}, {\n" + "  \"attributes\": {\n" + "    \"link\": \"http://www.baidu.com\"\n" + "  },\n" + "  \"insert\": \"百度2\"\n" + "}, {\n" + "  \"insert\": \"\\n\"\n" + "}, {\n" + "  \"attributes\": {\n" + "    \"italic\": true\n" + "  },\n" + "  \"insert\": \"居\"\n" + "}, {\n" + "  \"insert\": \"左2\"\n" + "}, {\n" + "  \"attributes\": {\n" + "    \"header\": 2\n" + "  },\n" + "  \"insert\": \"\\n\"\n" + "}, {\n" + "  \"insert\": \"居\"\n" + "}, {\n" + "  \"attributes\": {\n" + "    \"italic\": true\n" + "  },\n" + "  \"insert\": \"中\"\n" + "}, {\n" + "  \"attributes\": {\n" + "    \"bold\": true\n" + "  },\n" + "  \"insert\": \"1\"\n" + "}, {\n" + "  \"attributes\": {\n" + "    \"align\": \"center\"\n" + "  },\n" + "  \"insert\": \"\\n\"\n" + "}, {\n" + "  \"insert\": {\n" + "    \"image\": \"https://woneng-oss.oss-cn-hangzhou.aliyuncs.com/20210207/38266f774afb4ab6b189ae67905248db.png\"\n" + "  }\n" + "}, {\n" + "  \"attributes\": {\n" + "    \"align\": \"center\"\n" + "  },\n" + "  \"insert\": \"\\n\"\n" + "}, {\n" + "  \"insert\": \"居右1\"\n" + "}, {\n" + "  \"attributes\": {\n" + "    \"align\": \"right\"\n" + "  },\n" + "  \"insert\": \"\\n\"\n" + "}, {\n" + "  \"insert\": {\n" + "    \"video\": \"asdfasdfad\"\n" + "  }\n" + "}, {\n" + "  \"attributes\": {\n" + "    \"align\": \"center\",\n" + "    \"header\": 2\n" + "  },\n" + "  \"insert\": \"\\n\"\n" + "}, {\n" + "  \"insert\": \"剧中2\"\n" + "}, {\n" + "  \"attributes\": {\n" + "    \"align\": \"center\",\n" + "    \"header\": 2\n" + "  },\n" + "  \"insert\": \"\\n\"\n" + "}, {\n" + "  \"insert\": \"\\n爱的\"\n" + "}, {\n" + "  \"attributes\": {\n" + "    \"link\": \"http://www.baidu.com\"\n" + "  },\n" + "  \"insert\": \"发声发\"\n" + "}, {\n" + "  \"insert\": \"到付\"\n" + "}, {\n" + "  \"attributes\": {\n" + "    \"header\": 2\n" + "  },\n" + "  \"insert\": \"\\n\"\n" + "}, {\n" + "  \"insert\": \"\\n\\n\\n\"\n" + "}]"

        initLinearRecyclerView(viewBinding.recyclerView).also {
            it.adapter = object :QuillJsAdapter<QuillJsParamBean>(){
                override fun addOtherItemType() {

                }

                override fun convertOther(holder: BaseViewHolder, item: QuillJsParamBean) {

                }

                override fun appendQuillJsContent(list: ArrayList<QuillJsParamBean>) {
                    setNewInstance(list)
                }
            }.apply {
                appendQuillJs(content)
            }
        }
    }

    override fun initOnClick() {

    }

    override fun initData() {

    }

}