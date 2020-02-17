package com.ifran.journalapp

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Switch
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.ifran.journalapp.ui.NoteViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_tag_list.view.*

class TagListFragment : Fragment() {

    private val noteViewModel: NoteViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tag_list, container, false)
        val tagsIcon = requireActivity().resources.getDrawable(R.drawable.ic_label_black_24dp)

        val menu = view.navigation.menu
        val tagsMenu = menu.addSubMenu(R.id.menu_tags_secondary, 0, Menu.NONE,"All Labels")
        val multiTagSwitch = (view.navigation.menu.findItem(R.id.action_multi_tag_switch).actionView as RelativeLayout)[0] as Switch

        noteViewModel.allTags.observe(viewLifecycleOwner,  androidx.lifecycle.Observer { tags ->
            tagsMenu.clear()
            tags.forEachIndexed { idx, tag ->  tagsMenu.add(R.id.menu_tags_secondary, idx, Menu.NONE, tag.name).icon = tagsIcon}

            val navigationOnItemSelectedListener = NavigationView.OnNavigationItemSelectedListener { menuItem ->
                // TODO: Draw a logic table
                if (menuItem.groupId == R.id.menu_tags_primary) {
                    menu.forEach { it.isChecked = false }
                    tagsMenu.forEach { it.isChecked = false }
                    multiTagSwitch.isChecked = false
                }
                else if (!multiTagSwitch.isChecked) tagsMenu.forEach { it.isChecked = false }

                menuItem.isChecked = !menuItem.isChecked

                when (menuItem.itemId) {
                    R.id.action_tag_filter_all -> noteViewModel.setTagsFilter(emptyList())
                    R.id.action_tag_filter_unlabeled -> noteViewModel.setTagsFilterNoTagsAssigned()
                    R.id.action_multi_tag_switch-> {
                        multiTagSwitch.isChecked = !multiTagSwitch.isChecked
                        menuItem.isChecked = false
                    }
                    else -> {
                        noteViewModel.setTagsFilter (
                            when (multiTagSwitch.isChecked) {
                                true -> {
                                    // TODO: Add tag manipulation functions to viewmodel
                                    tagsMenu.iterator().asSequence().filter { it.isChecked }.map { tags[it.itemId] }.toList()
                                }
                                false -> {
                                    requireActivity().drawer_layout.closeDrawer(GravityCompat.START)
                                    listOf(tags[menuItem.itemId])
                                }
                            }
                        )
                    }
                }
                true
            }

            view.navigation.setNavigationItemSelectedListener(navigationOnItemSelectedListener)
        })
        return view
    }
}