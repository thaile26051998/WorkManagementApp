package com.hcmus.easywork.ui.auth.integrated;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.hcmus.easywork.R;
import com.hcmus.easywork.models.user.IntegratedUser;
import com.hcmus.easywork.viewmodels.auth.AuthenticationViewModel;

public class FragmentGoogleAuthentication extends Fragment {
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;
    private AuthenticationViewModel authViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), signInOptions);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthenticationViewModel.class);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            authenticate(account);
        } catch (ApiException e) {
            e.printStackTrace();
            getNavController().popBackStack();
        }
    }

    private void authenticate(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            IntegratedUser integratedUser = new IntegratedUser() {
                {
                    setEmail(account.getEmail());
                    setDisplayName(account.getDisplayName());
                    setPassword(account.getEmail());
                    setGoogleId(account.getId());
                }
            };
            Uri photo = account.getPhotoUrl();
            if (photo != null) {
                integratedUser.setAvatarUrl(photo.toString());
            }
            authViewModel.useGoogle(integratedUser);
            getNavController().popBackStack();
        }
    }

    private NavController getNavController() {
        return NavHostFragment.findNavController(this);
    }
}
