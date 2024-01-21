Team KITKAT_WATCH

Name - Sid - Contribution
Le Duy Quang - s3917105 - 20%
Nguyen Tuong Khang - s3927112 - 20%
Doan Tran Thien Phuc - s3926377 - 20%
Do Phuong Linh - s3926823 - 20%
Ngo Quoc Binh - s3927469 - 20%



Functionalities:

Sign in/ Sign up:
  Sign in by email, phone
  Check and validate sign in information
  Sign up by email / phone
  Check unique email / phone
  Sign up and sign in by phone send OTP to user's phone for verification
  Check missing field sign up
  Keep log in status (so the next time you open the app, you don't have to log in again)
  Log out

Home:
  Tailored post feeding feature
  Create post
  Display post with user name and avatar, date upload/modified, post image and caption, pet tags, like and comment number.
  Edit your owned post
  Report post
  Like post
  Add your comments
  View others comments (and their name and avatar)
  Like comments

Create/Edit/Delete Post:
  Upload an image or none
  Add a caption or none
  Tag your pets (zero to many pets) to your post
  Hashtag your post (which can be used to search for relevant posts)
  Save your editing
  Delete post

Search:
  Search by Posts, Communities, Users
  For Posts search, user can search by caption content or tag
  User can search communities or user by name
  User can join a community

Communities:
  User can create their own community or joined the others
  View posts in the communities
  Create post private to the community that you are a member
  Edit/delete your post in the community
  User that joined can leave the community
  Owner can delete the community
  
Messaging:
  View chat history and continue chat
  Display username and latest chat from each user/community
  Start communities chat
  Start 1 to 1 chat
  Send chat to user or community group
  Video call to user

Profile:
  User can edit their information
  View all their posts
  Add new pets
  Pets management
  Edit their pets profile and the pet images
  Recollect images that you tags your pets
  Delete your pets

Notification - (also can view on side nav):
  Display number of notifications
  Save the notifications and display from latest
  Ping notification to your phone using FCM

Follower / Following :
  Add/Remove follower
  View your followers/following
  Swipe to switch the screen of follower/following
  Start message with your follower/following
  Search follower/following
  Click on follower/following to see their profile page
  User can continuously view the follower/following of other users and can go back to the previous screen.

Admin (Separate App): 
  Display general information of the app
  Receive notification from user's report
  View user, post, communities
  Search by id/description/tag
  Admin can edit user and post
  Admin can ban user
  Admin can delete community
  Log out, does not remember log in session -> More secure



Tech Stack:

FCM:
  We use this service to ping notifications to users.

Firebase Authentication:
  We use this service for authentication users by email and password.

Firebase Firestore:
  We use this service as the database of our application.

Firebase Storage:
  We use this service as the storage for images.

Zegocloud:
  We use this service for the video call function of our application.

Google Maps:
  We use this service for the location sharing function of our application.
