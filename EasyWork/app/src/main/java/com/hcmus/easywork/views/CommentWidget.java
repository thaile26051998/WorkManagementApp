package com.hcmus.easywork.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.LayoutCommentWidgetBinding;
import com.hcmus.easywork.ui.chat.mention.SuggestedPeople;
import com.linkedin.android.spyglass.suggestions.SuggestionsResult;
import com.linkedin.android.spyglass.suggestions.interfaces.SuggestionsVisibilityManager;
import com.linkedin.android.spyglass.tokenization.QueryToken;
import com.linkedin.android.spyglass.tokenization.impl.WordTokenizer;
import com.linkedin.android.spyglass.tokenization.impl.WordTokenizerConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommentWidget extends ConstraintLayout {
    private LayoutCommentWidgetBinding binding;
    private Context mContext;

    private String mHint;

    private OnCommentListener mOnCommentListener;
    private OnPhotoAttachedListener mOnPhotoAttachedListener;
    private OnFileAttachedListener mOnFileAttachedListener;
    private OnMentionQueriedListener mOnMentionQueriedListener;
    private WordTokenizerConfig wordTokenizerConfig;
    private List<SuggestedPeople> mAvailablePeople;

    public CommentWidget(Context context) {
        this(context, null);
    }

    public CommentWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommentWidget);
        this.mHint = typedArray.getString(R.styleable.CommentWidget_widget_hint);
        typedArray.recycle();

        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = LayoutCommentWidgetBinding.inflate(inflater, this, true);

        wordTokenizerConfig = new WordTokenizerConfig.Builder().build();
        mAvailablePeople = new ArrayList<>();
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        TypedValue typedValue = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.colorControlNormal, typedValue, true);
        int color = ContextCompat.getColor(mContext, typedValue.resourceId);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(5f);
        canvas.drawLine(0f, 0f, getWidth(), 0f, paint);
        canvas.save();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        binding.edtComment.setTokenizer(new WordTokenizer(wordTokenizerConfig));
        binding.edtComment.setHint(mHint);

        binding.edtComment.setQueryTokenReceiver(queryToken -> {
            List<String> buckets = Collections.singletonList("people-network");
            List<SuggestedPeople> suggestedPeople = getSuggestions(queryToken);
            SuggestionsResult result = new SuggestionsResult(queryToken, suggestedPeople);
            @SuppressWarnings("unchecked")
            List<SuggestedPeople> suggestions = (List<SuggestedPeople>) result.getSuggestions();
            if (mOnMentionQueriedListener != null) {
                mOnMentionQueriedListener.onQueryReceived(suggestions);
            }
            return buckets;
        });

        // Auto hide/show send button
        binding.edtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean textNotEmpty = s.length() != 0;
                binding.btnSend.setVisibility(textNotEmpty ? View.VISIBLE : View.GONE);
            }
        });

        binding.btnSend.setOnClickListener(v -> {
            String message = binding.edtComment.getEditableText().toString();
            if (mOnCommentListener != null) {
                boolean sent = mOnCommentListener.onCommented(message);
                if (sent) {
                    // Commenting is done, clear the edit text
                    binding.edtComment.getEditableText().clear();
                    binding.edtComment.clearFocus();
                }
            }
        });

        // TODO: find solutions
        binding.btnAttachPhoto.setOnClickListener(v -> {
            if (mOnPhotoAttachedListener != null) {
                mOnPhotoAttachedListener.onPhotoAttached();
            }
        });

        binding.btnAttachFile.setOnClickListener(v -> {
            if (mOnFileAttachedListener != null) {
                mOnFileAttachedListener.onFileAttached();
            }
        });
    }

    @NonNull
    private List<SuggestedPeople> getSuggestions(QueryToken queryToken) {
        String[] namePrefixes = queryToken.getKeywords().toLowerCase().split(" ");
        List<SuggestedPeople> result = new ArrayList<>();
        for (SuggestedPeople sp : mAvailablePeople) {
            String name = sp.getName().toLowerCase();
            for (String namePrefix : namePrefixes) {
                if (name.startsWith(namePrefix)) {
                    result.add(sp);
                    break;
                }
            }
        }
        return result;
    }

    public void setOnCommentListener(OnCommentListener onCommentListener) {
        this.mOnCommentListener = onCommentListener;
    }

    public void setOnPhotoAttachedListener(OnPhotoAttachedListener onPhotoAttachedListener) {
        this.mOnPhotoAttachedListener = onPhotoAttachedListener;
    }

    public void setOnFileAttachedListener(OnFileAttachedListener onFileAttachedListener) {
        this.mOnFileAttachedListener = onFileAttachedListener;
    }

    public void setOnMentionQueriedListener(OnMentionQueriedListener listener) {
        this.mOnMentionQueriedListener = listener;
    }

    public void setAvailablePeople(List<SuggestedPeople> people) {
        this.mAvailablePeople = people;
    }

    public void setSuggestionsVisibilityManager(@Nullable final SuggestionsVisibilityManager suggestionsVisibilityManager) {
        binding.edtComment.setSuggestionsVisibilityManager(suggestionsVisibilityManager);
    }

    public void insertMention(SuggestedPeople people) {
        binding.edtComment.insertMention(people);
    }

    public void requestTextFieldFocus() {
        binding.edtComment.requestFocus();
    }

    public interface OnCommentListener {
        boolean onCommented(String commentText);
    }

    public interface OnPhotoAttachedListener {
        void onPhotoAttached();
    }

    public interface OnFileAttachedListener {
        void onFileAttached();
    }

    public interface OnMentionQueriedListener {
        void onQueryReceived(@NonNull List<SuggestedPeople> queryResult);
    }
}
