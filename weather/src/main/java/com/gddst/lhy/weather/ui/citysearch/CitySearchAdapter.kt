package com.gddst.lhy.weather.ui.citysearch

import android.view.View
import android.widget.TextView
import com.com.sky.downloader.greendao.CityVoDao
import com.gddst.app.lib_common.base.BaseActivity
import com.gddst.app.lib_common.base.BaseApplication
import com.gddst.app.lib_common.commonAdapter.recycleView.CommonRecycleViewAdapter
import com.gddst.app.lib_common.commonAdapter.recycleView.base.ViewHolder
import com.gddst.app.lib_common.weather.db.CityVo
import com.gddst.app.lib_common.widgets.SignKeyWordTextView
import com.gddst.lhy.weather.R
import org.greenrobot.eventbus.EventBus

class CitySearchAdapter(activity: BaseActivity, layoutid:Int= R.layout.item_search_text,search_cityList :MutableList<CityVo>)
    : CommonRecycleViewAdapter<CityVo>(activity,layoutid,search_cityList) {
    override fun convert(viewHolder: ViewHolder, cityVo: CityVo?, position: Int) {
        val item_text = viewHolder.getView<SignKeyWordTextView?>(R.id.item_search_text)
        item_text?.setTextColor(mContext.resources.getColor(R.color.point_facility11))
        item_text?.setBackgroundColor(mContext.resources.getColor(R.color.white))
//        //设置高亮关键字
//        item_text?.setSignText(searchText)
        //设置高亮颜色
        item_text?.setSignTextColor(mContext.resources.getColor(R.color.salmon))
        item_text?.setText(
                cityVo?.getLocation() + "," + cityVo?.getParent_city() + "," + cityVo?.getAdmin_area(),
                TextView.BufferType.NORMAL)
        viewHolder.setOnClickListener(R.id.item_search_text, View.OnClickListener { EventBus.getDefault().post(cityVo) })
        //判断本地数据库中是否有该城市
        val cityVoDbList = BaseApplication.getIns().daoSession.cityVoDao
                .queryBuilder().where(CityVoDao.Properties.Cid.eq(cityVo?.getCid())).list()
        if (cityVoDbList.size > 0) {
            viewHolder.setText(R.id.item_search_hint, mContext.getString(R.string.added))
            viewHolder.setTextColor(R.id.item_search_hint, mContext.resources.getColor(R.color.lightseagreen))
            viewHolder.setVisible(R.id.item_search_hint, View.VISIBLE)
        } else {
            viewHolder.setText(R.id.item_search_hint, "")
            viewHolder.setTextColor(R.id.item_search_hint, mContext.resources.getColor(R.color.lightseagreen))
            viewHolder.setVisible(R.id.item_search_hint, View.GONE)
        }
    }
}