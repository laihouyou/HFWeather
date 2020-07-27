package com.gddst.lhy.weather.ui.citysearch

import android.view.View
import android.widget.TextView
import com.gddst.app.lib_common.base.BaseActivity
import com.gddst.app.lib_common.commonAdapter.recycleView.CommonRecycleViewAdapter
import com.gddst.app.lib_common.commonAdapter.recycleView.base.ViewHolder
import com.gddst.app.lib_common.weather.db.CityVo
import com.gddst.lhy.weather.R
import com.gddst.lhy.weather.util.WeatherUtil
import org.greenrobot.eventbus.EventBus

class CityHostAdapter(activity: BaseActivity, layoutId:Int= R.layout.item_text,cityhostList:MutableList<CityVo>)
    : CommonRecycleViewAdapter<CityVo>(activity,layoutId,cityhostList) {
    override fun convert(viewHolder: ViewHolder, cityVo: CityVo?, position: Int) {
        val item_text = viewHolder.getView<TextView?>(R.id.item_text)
        item_text?.setText(cityVo?.getLocation())
        //如果是定位城市，动态设置定位图标
        if (cityVo?.getCityType() == WeatherUtil.city_location) {
            val drawableLeft =  mContext.resources.getDrawable(R.drawable.city_loaction)
            drawableLeft.setBounds(0, 0, drawableLeft.minimumWidth, drawableLeft.minimumHeight)
            item_text?.setCompoundDrawables(drawableLeft, null, null, null)
        } else {
            item_text?.setCompoundDrawables(null, null, null, null)
        }
        if (cityVo!!.getIsSelected()) {
            item_text?.setTextColor( mContext.resources.getColor(R.color.cornflowerblue12))
            item_text?.setBackgroundResource(R.drawable.city_search_host_list_selected)
        } else {
            item_text?.setTextColor( mContext.resources.getColor(R.color.point_facility11))
            item_text?.setBackgroundResource(R.drawable.city_search_host_list_no_selected)
        }
        viewHolder.setOnClickListener(R.id.item_text, View.OnClickListener { EventBus.getDefault().post(cityVo) })
    }
}