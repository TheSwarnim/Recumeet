package com.hackathon.recumeet.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.brouding.doubletaplikeview.DoubleTapLikeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.recumeet.Feed.LikeCommentActivity;
import com.hackathon.recumeet.Models.Post;
import com.hackathon.recumeet.Models.User;
import com.hackathon.recumeet.R;
import com.hackathon.recumeet.UserProfile.ProfileActivity;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Post> mPosts;

    private FirebaseUser fUser;
    private AlertDialog.Builder ad;

    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        final Post post = mPosts.get(position);
        isLiked(post.getPostId(), holder.like);
        noLikes(post.getPostId(), holder.noOfLikes);
        noOfComments(post.getPostId(), holder.noOfComments);
        getComments(post.getPostId(), holder.noOfComments);

        if (post.getDescription() != null) {
            holder.description.setText(post.getDescription());
        } else {
            holder.description.setVisibility(View.GONE);
        }

        if (post.getImageUri() != null) {
            Picasso.get().load(post.getImageUri()).fit().centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(holder.doubleTapLikeView.imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(post.getImageUri()).fit().centerCrop().into(holder.doubleTapLikeView.imageView);
                }
            });
        } else {
            holder.doubleTapLikeView.setVisibility(View.GONE);
        }
        final ProgressDialog pd = new ProgressDialog(mContext);
        pd.setMessage("Please Wait");
        pd.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(post.getPublisher());
        ref.keepSynced(true);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final User user = snapshot.getValue(User.class);
                assert user != null;
                holder.username.setText(user.getName());
                holder.bio.setText(user.getBio());

                Picasso.get().load(user.getProfileUri()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imageProfile, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(user.getProfileUri()).into(holder.imageProfile);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });

        pd.dismiss();

        holder.like.setOnClickListener(v -> likePost(holder.like, post));

        holder.no_comments_text.setOnClickListener(v -> intentFunction(post));

        holder.comment.setOnClickListener(v -> intentFunction(post));

        holder.no_likes_text.setOnClickListener(v -> intentFunction(post));

        holder.noOfComments.setOnClickListener(v -> intentFunction(post));

        holder.noOfLikes.setOnClickListener(v -> intentFunction(post));

        holder.imageProfile.setOnClickListener(v -> openProfile(post));

        holder.username.setOnClickListener(v -> openProfile(post));

        holder.doubleTapLikeView.setOnTapListener(new DoubleTapLikeView.OnTapListener() {
            @Override
            public void onDoubleTap(View view) {
                likePost(holder.like, post);
            }

            @Override
            public void onTap() {

            }
        });

        holder.more.setOnClickListener(v -> {
            if (post.getPublisher().equals(fUser.getUid())) {
                PopupMenu popup = new PopupMenu(Objects.requireNonNull(mContext), holder.more);

                popup.getMenuInflater().inflate(R.menu.current_user_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.delete) {
                        ad = new AlertDialog.Builder(mContext);
                        ad.setTitle("Delete");
                        ad.setMessage("Are you sure to delete the post");
                        ad.setCancelable(true);

                        ad.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            FirebaseDatabase.getInstance().getReference().child("posts").child(post.getPostId()).removeValue();
                            Toast.makeText(mContext, "Post Removed Successfully", Toast.LENGTH_SHORT).show();
                        }).setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss());

                        AlertDialog alert11 = ad.create();
                        alert11.show();

                    }
                    return true;
                });

                popup.show();
            } else {
                PopupMenu popup = new PopupMenu(Objects.requireNonNull(mContext), holder.more);

                popup.getMenuInflater().inflate(R.menu.user_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.redirect) {
                        openProfile(post);
                    }
                    return true;
                });

                popup.show();
            }
        });

    }

    private void likePost(ImageView like, Post post) {
        if (like.getTag().equals("like")) {
            FirebaseDatabase.getInstance().getReference().child("likes").
                    child(post.getPostId()).child(fUser.getUid()).child("uId").setValue(fUser.getUid());
        } else {
            FirebaseDatabase.getInstance().getReference().child("likes").
                    child(post.getPostId()).child(fUser.getUid()).removeValue();
        }
    }

    private void openProfile(Post post) {
        Intent intent = new Intent(mContext, ProfileActivity.class);
        intent.putExtra("publisherId", post.getPublisher());
        mContext.startActivity(intent);
    }

    private void intentFunction(Post post) {
        Intent intent = new Intent(mContext, LikeCommentActivity.class);
        intent.putExtra("postId", post.getPostId());
        intent.putExtra("authorId", post.getPublisher());
        mContext.startActivity(intent);
    }

    private void noOfComments(String postId, final TextView noOfComments) {
        FirebaseDatabase.getInstance().getReference().child("comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String noOfCom = snapshot.getChildrenCount() + " ";
                noOfComments.setText(noOfCom);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    private void noLikes(String postId, final TextView noOfLikes) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("likes").child(postId);
        ref.keepSynced(true);

        ref.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String noOfLik = dataSnapshot.getChildrenCount() + " ";
                noOfLikes.setText(noOfLik);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getComments(String postId, final TextView noOfComments) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("comments").child(postId);
        ref.keepSynced(true);

        ref.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String noOfCom = dataSnapshot.getChildrenCount() + " ";
                noOfComments.setText(noOfCom);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void isLiked(String postId, final ImageView imageView) {
        FirebaseDatabase.getInstance().getReference().child("likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(fUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_heart_red);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_heart2);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("likes").child(postId).keepSynced(true);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageProfile;
        public ImageView like;
        public ImageView comment;
        public ImageView more;

        public TextView username;
        public TextView bio;
        public TextView noOfLikes;
        public TextView noOfComments;
        public TextView no_likes_text;
        public TextView no_comments_text;

        SocialTextView description;
        DoubleTapLikeView doubleTapLikeView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            doubleTapLikeView = itemView.findViewById(R.id.layout_double_tap_like);
            more = itemView.findViewById(R.id.post_more);
            no_likes_text = itemView.findViewById(R.id.no_likes_text);
            imageProfile = itemView.findViewById(R.id.profile_pic);
            no_comments_text = itemView.findViewById(R.id.no_comments_text);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            username = itemView.findViewById(R.id.username);
            bio = itemView.findViewById(R.id.bio);
            noOfLikes = itemView.findViewById(R.id.no_likes);
            noOfComments = itemView.findViewById(R.id.no_comments);
            description = itemView.findViewById(R.id.post_description);
        }
    }
}
