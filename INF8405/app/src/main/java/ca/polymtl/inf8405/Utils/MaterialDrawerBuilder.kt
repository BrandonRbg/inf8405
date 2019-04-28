package ca.polymtl.inf8405.Utils

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import ca.polymtl.inf8405.R
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem

fun AppCompatActivity.buildDrawer(toolbar: Toolbar, selectedItem: Int, onItemSelect: (Int) -> Unit) = DrawerBuilder()
    .withActivity(this)
    .withToolbar(toolbar)
    .addDrawerItems(
        PrimaryDrawerItem().withName("Chat").withIcon(R.drawable.ic_message_black_24dp),
        PrimaryDrawerItem().withName("Map").withIcon(R.drawable.ic_map_black_24dp),
        PrimaryDrawerItem().withName("Stats").withIcon(R.drawable.ic_data_usage_black_24dp)
    )
    .withSelectedItemByPosition(selectedItem)
    .withOnDrawerItemClickListener { view, position, drawerItem ->
        onItemSelect(position)
        true
    }
    .build()