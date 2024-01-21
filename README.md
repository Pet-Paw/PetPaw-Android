Team KITKAT_WATCH<br />
<br />
Name - Sid - Contribution<br />
Le Duy Quang - s3917105 - 20%<br />
Nguyen Tuong Khang - s3927112 - 20%<br />
Doan Tran Thien Phuc - s3926377 - 20%<br />
Do Phuong Linh - s3926823 - 20%<br />
Ngo Quoc Binh - s3927469 - 20%<br />
<br />
<br />
<br />
Functionalities:<br />
<br />
Sign in/ Sign up:<br />
  Sign in by email, phone<br />
  Check and validate sign in information<br />
  Sign up by email / phone<br />
  Check unique email / phone<br />
  Sign up and sign in by phone send OTP to user's phone for verification<br />
  Check missing field sign up<br />
  Keep log in status (so the next time you open the app, you don't have to log in again)<br />
  Log out<br />
<br />
Home:<br />
  Tailored post feeding feature<br />
  Create post<br />
  Display post with user name and avatar, date upload/modified, post image and caption, pet tags, like and comment number.<br />
  Edit your owned post<br />
  Report post<br />
  Like post<br />
  Add your comments<br />
  View others comments (and their name and avatar)<br />
  Like comments<br />
<br />
Create/Edit/Delete Post:<br />
  Upload an image or none<br />
  Add a caption or none<br />
  Tag your pets (zero to many pets) to your post<br />
  Hashtag your post (which can be used to search for relevant posts)<br />
  Save your editing<br />
  Delete post<br />
<br />
Search:<br />
  Search by Posts, Communities, Users<br />
  For Posts search, user can search by caption content or tag<br />
  User can search communities or user by name<br />
  User can join a community<br />
<br />
Communities:<br />
  User can create their own community or joined the others<br />
  View posts in the communities<br />
  Create post private to the community that you are a member<br />
  Edit/delete your post in the community<br />
  User that joined can leave the community<br />
  Owner can delete the community<br />
<br />
Messaging:<br />
  View chat history and continue chat<br />
  Display username and latest chat from each user/community<br />
  Start communities chat<br />
  Start 1 to 1 chat<br />
  Send chat to user or community group<br />
  Video call to user<br />
<br />
Profile:<br />
  User can edit their information<br />
  View all their posts<br />
  Add new pets<br />
  Pets management<br />
  Edit their pets profile and the pet images<br />
  Recollect images that you tags your pets<br />
  Delete your pets<br />
<br />
Notification - (also can view on side nav):<br />
  Display number of notifications<br />
  Save the notifications and display from latest<br />
  Ping notification to your phone using FCM<br />
<br />
Follower / Following :<br />
  Add/Remove follower<br />
  View your followers/following<br />
  Swipe to switch the screen of follower/following<br />
  Start message with your follower/following<br />
  Search follower/following<br />
  Click on follower/following to see their profile page<br />
  User can continuously view the follower/following of other users and can go back to the previous screen.<br />
<br />
Admin (Separate App): <br />
  Display general information of the app<br />
  Receive notification from user's report<br />
  View user, post, communities<br />
  Search by id/description/tag<br />
  Admin can edit user and post<br />
  Admin can ban user<br />
  Admin can delete community<br />
  Log out, does not remember log in session -> More secure<br />
<br />
<br />
<br />
Tech Stack:<br />
<br />
FCM:<br />
  We use this service to ping notifications to users.<br />
<br />
Firebase Authentication:<br />
  We use this service for authentication users by email and password.<br />
<br />
Firebase Firestore:<br />
  We use this service as the database of our application.<br />
<br />
Firebase Storage:<br />
  We use this service as the storage for images.<br />
<br />
Zegocloud:<br />
  We use this service for the video call function of our application.<br />
<br />
Google Maps:<br />
  We use this service for the location sharing function of our application.<br />
