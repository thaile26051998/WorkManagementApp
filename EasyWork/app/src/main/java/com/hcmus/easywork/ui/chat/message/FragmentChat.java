package com.hcmus.easywork.ui.chat.message;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewStubProxy;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.easywork.R;
import com.hcmus.easywork.data.util.UserDataLookup;
import com.hcmus.easywork.databinding.FragmentChatBinding;
import com.hcmus.easywork.databinding.ItemMessageBinding;
import com.hcmus.easywork.databinding.StubMessageFileBinding;
import com.hcmus.easywork.databinding.StubMessageImageBinding;
import com.hcmus.easywork.models.chat.Message;
import com.hcmus.easywork.models.chat.group.GroupMember;
import com.hcmus.easywork.models.file.EwFile;
import com.hcmus.easywork.twilio.voice.TwilioVoiceCallViewModel;
import com.hcmus.easywork.ui.chat.image.SharedImageViewModel;
import com.hcmus.easywork.ui.chat.mention.AdapterSuggestedPeople;
import com.hcmus.easywork.ui.chat.mention.SuggestedPeople;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.ui.file.MediaPicker;
import com.hcmus.easywork.utils.AppFileWriter;
import com.hcmus.easywork.utils.ImageLoadingLibrary;
import com.hcmus.easywork.utils.UtilsView;
import com.hcmus.easywork.viewmodels.chat.SingleGroupViewModel;
import com.hcmus.easywork.views.ExtendedMaterialToolBar;
import com.linkedin.android.spyglass.suggestions.interfaces.SuggestionsVisibilityManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FragmentChat extends BaseFragment<FragmentChatBinding>
        implements ExtendedMaterialToolBar.OnMenuItemClickListener {
    private DialogChatMessage dialogChatMessage;
    private AdapterMessage adapter;
    private SingleGroupViewModel groupViewModel;
    private boolean isSingleChat = false;
    private SharedImageViewModel sharedImageViewModel;
    private ItemMessageBinding pinnedMessageBinding;
    private TwilioVoiceCallViewModel twilioVoiceCallViewModel;
    private int callerId = 0; // temporary solution
    private AdapterSuggestedPeople adapterSuggestedPeople;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_chat;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Set windowSoftInputMode to ADJUST_RESIZE to show section comment while keyboard is displayed
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onDetach() {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onDetach();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupViewModel = createViewModel(SingleGroupViewModel.class);
        adapter = new AdapterMessage(activity);
        dialogChatMessage = new DialogChatMessage();
        sharedImageViewModel = createViewModel(SharedImageViewModel.class);
        twilioVoiceCallViewModel = createViewModel(TwilioVoiceCallViewModel.class);
        adapterSuggestedPeople = new AdapterSuggestedPeople(activity);
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.toolbar.setNavigationOnClickListener(v -> getNavController().navigateUp());
        binding.toolbar.setOnMenuItemClickListener(this);
        pinnedMessageBinding = binding.pinnedMessage;

        setupAdapterMessage();
        setupChatWidget();
        setupMention();

        binding.btnUnpin.setOnClickListener(l -> groupViewModel.pinMessage(pinnedMessageBinding.getMessage()));

        groupViewModel.fetchMessage();
        groupViewModel.getPinnedMessage();
    }

    private void setupAdapterMessage() {
        adapter.setOnClickListener((object, position) -> {

        });

        adapter.setOnLongClickListener((object, position) -> {
            dialogChatMessage.setOnMenuSelectedListener(menuId -> {
                switch (menuId) {
                    case R.id.action_pin: {
                        groupViewModel.pinMessage(object);
                        break;
                    }
                    case R.id.action_like: {
                        groupViewModel.likeMessage(object.getId());
                        //groupViewModel.unlikeMessage(object.getId());
                        break;
                        // TODO: like message
                    }
//                    case R.id.action_copy: {
//                        break;
//                        // TODO: copy message
//                    }
                    case R.id.action_remove: {
                        groupViewModel.deleteMessage(object.getId());
                        break;
                    }
                }
            });
            dialogChatMessage.show(activity.getSupportFragmentManager(), "show_dialog");
        });

        adapter.setOnSharedImageClickListener(file -> {
            sharedImageViewModel.selectFile(file);
            getNavController().navigate(R.id.action_view_shared_image);
        });

        adapter.setOnSharedFileClickListener(file -> makePopup(file.mName));
    }

    private void setupChatWidget() {
        // Equivalent to send message
        binding.wgChat.setOnCommentListener(commentText -> {
            Message message = new Message() {
                {
                    setContent(commentText);
                }
            };
            groupViewModel.send(message);
            return true;
        });

        binding.wgChat.setOnFileAttachedListener(() -> {
            MediaPicker mediaPicker = new MediaPicker(activity);
            mediaPicker.setOnFilePickedListener(uri -> {
                String path = uri.getPath();
                if (path != null) {
                    path = path.replace("/document/raw:", "");
                    File file = new File(path);
                    groupViewModel.sendFile(file, activity.getContentResolver().getType(uri));
                }
            });
            mediaPicker.apply();
        });

        binding.wgChat.setOnPhotoAttachedListener(() -> {
            MediaPicker mediaPicker = new MediaPicker(activity);
            mediaPicker.setOnImagePickedListener(uri -> {
                File f = AppFileWriter.getImageFileFromUri(activity, uri);
                if (f != null && f.exists()) {
                    String mime = activity.getContentResolver().getType(uri);
                    groupViewModel.sendFile(f, mime);
                }
            });
            mediaPicker.apply();
        });

        binding.wgChat.setOnMentionQueriedListener(queryResult -> {
            adapterSuggestedPeople.submitList(queryResult);
            binding.rvMentions.swapAdapter(adapterSuggestedPeople, true);
            boolean display = queryResult.size() > 0;
            showSuggestions(display);
        });

        binding.wgChat.setSuggestionsVisibilityManager(new SuggestionsVisibilityManager() {
            @Override
            public void displaySuggestions(boolean display) {
                showSuggestions(display);
            }

            @Override
            public boolean isDisplayingSuggestions() {
                return binding.rvMentions.getVisibility() == RecyclerView.VISIBLE;
            }
        });
    }

    private void setupMention() {
        adapterSuggestedPeople.setOnClickListener((object, position) -> {
            binding.wgChat.insertMention(object);
            binding.rvMentions.swapAdapter(new AdapterSuggestedPeople(activity), true);
            showSuggestions(false);
            binding.wgChat.requestTextFieldFocus();
        });
    }

    private void showSuggestions(boolean display) {
        binding.rvMentions.setVisibility(display ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        binding.setAdapter(adapter);
        binding.rvMentions.setAdapter(adapterSuggestedPeople);

        groupViewModel.get().observe(getViewLifecycleOwner(), group -> {
            binding.setItem(group);
            isSingleChat = group.isSingleChat();
            if (isSingleChat) {
                // If user himself creates the 1-1 chat, call to user.getUserId()
                // otherwise, call group.getCreatorId()
                if (groupViewModel.getUserId() == group.getCreatorId()){
                    callerId = group.getUserId();
                } else {
                    callerId = group.getCreatorId();
                }
                //callerId = group.getUserId();
            } else {
                binding.toolbar.getMenu().findItem(R.id.action_call).setVisible(false);
            }
            binding.wgChat.setAvailablePeople(getSuggestedPeople(group.getMembers()));
        });

        groupViewModel.getLoadingResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getLoadingState()) {
                case INIT:
                case LOADING: {
                    break;
                }
                case LOADED: {
                    adapter.submitList(result.getResult());
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });

        groupViewModel.getSendingResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    groupViewModel.fetchMessage();
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });

        groupViewModel.getDeletingMessageResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    makeSnack("Deleted");
                    groupViewModel.fetchMessage();
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });

        groupViewModel.getSendingFileResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    groupViewModel.fetchMessage();
                    makePopup(result.getResult().getMessage());
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                }
            }
        });

        groupViewModel.getPinningMessageResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT:
                    break;
                case DONE: {
                    groupViewModel.getPinnedMessage();
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });

        groupViewModel.getLoadingPinnedMessageResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT:
                    break;
                case DONE: {
                    Message message = result.getResult();
                    if (message == null) {
                        UtilsView.hide(binding.pinnedMessageContainer);
                    } else {
                        UtilsView.show(binding.pinnedMessageContainer);
                        bindPinnedMessage(message);
                    }
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });

        groupViewModel.getLoadingFileResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT:
                    break;
                case DONE: {
                    EwFile file = result.getResult();
                    if (file.mType.toLowerCase().contains("image")) {
                        inflateStubImage(file);
                    } else {
                        inflateStubFile(file);
                    }
                    break;
                }
                case FAILED: {
                    break;
                }
            }
        });

        groupViewModel.getLikeMessageResult().observe(getViewLifecycleOwner(), likeMessageResult -> {
            switch (likeMessageResult.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    groupViewModel.fetchMessage();
                    // WARNING: bad performance
                    break;
                }
                case FAILED: {
                    makePopup(likeMessageResult.getErrorMessage());
                    break;
                }
            }
        });

        groupViewModel.getUnlikeMessageResult().observe(getViewLifecycleOwner(), unlikeMessageResult -> {
            switch (unlikeMessageResult.getState()) {
                case INIT:
                    break;
                case DONE: {
                    groupViewModel.fetchMessage();
                    break;
                }
                case FAILED: {
                    makePopup(unlikeMessageResult.getErrorMessage());
                    break;
                }
            }
        });
    }

    private List<SuggestedPeople> getSuggestedPeople(List<GroupMember> members) {
        List<SuggestedPeople> list = new ArrayList<>();
        for (GroupMember gm : members) {
            SuggestedPeople people = new SuggestedPeople();
            people.setUserId(gm.getUserId());
            people.setName(gm.getUser().getDisplayName());
            list.add(people);
        }
        return list;
    }

    private void bindPinnedMessage(Message message) {
        pinnedMessageBinding.setMessage(message);
        UserDataLookup.find(message.getUserId()).setOnRecordFoundListener(userRecord -> {
            pinnedMessageBinding.name.setText(userRecord.getName());
            pinnedMessageBinding.avatar.setAvatarUsingGlide(userRecord.getAvatar());
        });
        if (message.getContent() == null || message.getContent().equals("")) {
            groupViewModel.getFile(message.getId());
        } else {
            pinnedMessageBinding.message.setVisibility(View.VISIBLE);
            UtilsView.hideViewStub(pinnedMessageBinding.stubImage);
            UtilsView.hideViewStub(pinnedMessageBinding.stubFile);
        }
    }

    private void inflateStubImage(EwFile file) {
        ViewStubProxy imageProxy = pinnedMessageBinding.stubImage;
        imageProxy.setOnInflateListener((stub, inflated) -> {
            StubMessageImageBinding imageBinding = DataBindingUtil.bind(inflated);
            if (imageBinding != null) {
                ImageLoadingLibrary.useContext(activity)
                        .load(file.mData.data)
                        .into(imageBinding.commentImage);
            }
        });
        UtilsView.inflateViewStub(imageProxy);
        UtilsView.hideViewStub(pinnedMessageBinding.stubFile);
    }

    private void inflateStubFile(EwFile file) {
        ViewStubProxy fileProxy = pinnedMessageBinding.stubFile;
        fileProxy.setOnInflateListener((stub, inflated) -> {
            StubMessageFileBinding fileBinding = DataBindingUtil.bind(inflated);
            if (fileBinding != null) {
                fileBinding.setFile(file);
            }
        });
        UtilsView.inflateViewStub(fileProxy);
        UtilsView.hideViewStub(pinnedMessageBinding.stubImage);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        @IdRes int navigateId = 0;
        boolean startCall = false;
        switch (item.getItemId()) {
            case R.id.action_call: {
                navigateId = isSingleChat ? R.id.action_open_single_voice_call
                        : R.id.action_open_group_voice_call;
                startCall = true;
                break;
            }
            case R.id.action_video_call: {
                navigateId = isSingleChat ? R.id.action_open_single_video_call
                        : R.id.action_open_group_video_call;
                break;
            }
            case R.id.action_info: {
                navigateId = isSingleChat ? R.id.action_show_personal_info
                        : R.id.action_show_group_info;
                break;
            }
        }
        if (navigateId != 0) {
            getNavController().navigate(navigateId);
            if (startCall && isSingleChat) {
                twilioVoiceCallViewModel.callTo(callerId);
            }
            return true;
        }
        return false;
    }
}
