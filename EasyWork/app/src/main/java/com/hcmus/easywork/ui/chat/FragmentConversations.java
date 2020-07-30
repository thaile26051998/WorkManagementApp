package com.hcmus.easywork.ui.chat;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.FragmentConversationsBinding;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.viewmodels.chat.ConversationViewModel;
import com.hcmus.easywork.viewmodels.chat.SingleGroupViewModel;

public class FragmentConversations extends BaseFragment<FragmentConversationsBinding>
        implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener,
        Toolbar.OnMenuItemClickListener {
    private AdapterConversation adapterConversation;
    private ConversationViewModel viewModel;
    private SingleGroupViewModel groupViewModel;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_conversations;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = createViewModel(ConversationViewModel.class);
        groupViewModel = createViewModel(SingleGroupViewModel.class);
        adapterConversation = new AdapterConversation(activity);
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.swipeLayout.setOnRefreshListener(this);
        binding.toolbar.setOnMenuItemClickListener(this);
        final SearchView searchView = ((SearchView) binding.toolbar.getMenu().findItem(R.id.action_search).getActionView());
        searchView.setQueryHint(getString(R.string.action_search_conversation));
        searchView.setOnQueryTextListener(this);

        adapterConversation.setOnClickListener((object, position) -> {
            groupViewModel.setGroup(object);
            getNavController().navigate(R.id.action_open_personal_conversation);
        });
        viewModel.getAllGroups();
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        binding.setAdapter(adapterConversation);

        viewModel.getLoadingResult().observe(getViewLifecycleOwner(), loadingResult -> {
            switch (loadingResult.getLoadingState()) {
                case INIT: {
                    // Nothing to observe
                    break;
                }
                case LOADING: {
                    // Enable refreshing icon
                    binding.swipeLayout.setRefreshing(true);
                    break;
                }
                case LOADED: {
                    // Bind loaded data
                    adapterConversation.setUserId(viewModel.getUserId());
                    adapterConversation.setList(loadingResult.getResult());
                    binding.swipeLayout.postDelayed(() -> binding.swipeLayout.setRefreshing(false), 500);
                    break;
                }
                case FAILED: {
                    // Notify error message and set empty list
                    makePopup(loadingResult.getErrorMessage());
                    adapterConversation.setList(loadingResult.getResult());
                    binding.swipeLayout.postDelayed(() -> binding.swipeLayout.setRefreshing(false), 500);
                    break;
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        binding.swipeLayout.post(() -> viewModel.getAllGroups());
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapterConversation.getFilter().filter(newText);
        return false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_start_conversation) {
            getNavController().navigate(R.id.action_start_conversation);
            return true;
        }
        return false;
    }
}
