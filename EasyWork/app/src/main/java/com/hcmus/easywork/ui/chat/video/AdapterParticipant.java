package com.hcmus.easywork.ui.chat.video;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.hcmus.easywork.ui.common.adapter.RecyclerListAdapter;
import com.hcmus.easywork.data.util.UserDataLookup;
import com.hcmus.easywork.databinding.ItemParticipantBinding;
import com.hcmus.easywork.models.twilio.Participant;

public class AdapterParticipant extends RecyclerListAdapter<Participant, ItemParticipantBinding> {
    private LifecycleOwner lifecycleOwner;

    public AdapterParticipant(Context context) {
        super(context);
    }

    public AdapterParticipant(Context context, @NonNull LifecycleOwner lifecycleOwner) {
        this(context);
        this.lifecycleOwner = lifecycleOwner;
    }

    @Override
    public ItemParticipantBinding getBindingObject(LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
        return ItemParticipantBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerListViewHolder<ItemParticipantBinding> holder, int position) {
        Participant participant = getItem(position);

        participant.getAudioEnabledState().observe(lifecycleOwner, isEnabled ->
                holder.binding.participantView.setAudioEnabled(isEnabled));
        participant.getVideoEnabledState().observe(lifecycleOwner, isEnabled ->
                holder.binding.participantView.setVideoEnabled(isEnabled));
        participant.getDominantSpeakingState().observe(lifecycleOwner, isSpeaking -> {
            boolean value = isSpeaking == null ? false : isSpeaking;
            holder.binding.participantView.setDominantSpeaking(value);
        });
        participant.getRenderingState().observe(lifecycleOwner, isRendering -> {
            if (isRendering == null) {
                participant.removeRenderer(holder.binding.participantView.getVideoView());
                holder.binding.participantView.clear();
            } else {
                if (isRendering) {
                    participant.getVideoTrack().addRenderer(holder.binding.participantView.getVideoView());
                } else {
                    participant.removeRenderer(holder.binding.participantView.getVideoView());
                    holder.binding.participantView.clear();
                }
            }
        });
        participant.mParticipantName.observe(lifecycleOwner, name -> {
            holder.binding.participantView.setParticipantName(name);
            if (name.startsWith("TWID")) {
                try {
                    int id = Integer.parseInt(name.substring(4));
                    UserDataLookup.find(id).setOnRecordFoundListener(userRecord ->
                            holder.binding.participantView.setParticipantName(userRecord.getName()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}
