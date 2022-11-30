# True Spotify Shuffle
 An actual Spotify shuffler, because Spotify's own shuffling is bad.

---

## Why this is made
Spotify's own shuffling is bad because it has an algorithm?? where it prefers certain songs over another because they are more profitable to them. Or it just gets stuck in a loop where it plays the same 20 songs in a loop. And since they removed the force shuffle button on a playlist, this is the only solution.

---

## How to use

<ul>
<li>Download and install the apk from the <a href="https://github.com/madmagic007/True-Spotify-Shuffle/releases" target="_blank">releases</a> page.</li>
<li>Open the app, it will prompt to authorize the app to your account. Close the page once authorized and go back to the app.</li>
<li>You must be listening to the playlist (on any device) you wish to have shuffled and click "Add current", it will now parse the playlist.</li>
<li>Once fully parsed (it says when done), you can tap "Shuffle selected" at any time (note: spotify must be playing on the desired device or the shuffling can't begin).</li>
<li>You can now enjoy actual shuffling of your playlist. If you with the shuffling to stop, expand the notification and press stop shuffle (you must have an active internet connection to use the shuffling, even when listening to downloaded songs/playlists).</li>
<li>It's important that you reparse the playlist every time you add/remove song (more info below).</li>
</ul>

---

## How it works

This app stores the id of all songs in the selected playlist and tells the spotify api to play one of the songs actually randomly picked from the list.
The app stores the song ids so it doesn't have to retrieve all the songs through the api each time. In case the playlist has been updated (songs added / removed), you must reparse the playlist, this updates the stored list of ids.
