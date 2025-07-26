---
title: 03 Planning & Coordination
---
# Team Members

* Team coordinator - Ivan Lichner, 01226385, e1226385@student.tuwien.ac.at 
* Technical architect - Christoph Winkler, 11701292, e11701292@student.tuwien.ac.at 
* Test coordinator - Valentin Kastberger, 11905155, e11905155@student.tuwien.ac.at 
* Documentation coordinator - Bianca Träger, 01326815, bianca.traeger@tuwien.ac.at 
* UI architect - Hariton Buçka, 01529018, e1529018@student.tuwien.ac.at 
* DevOps coordinator - Simon Ripphausen, 12444081, e12444081@student.tuwien.ac.at 

# Milestone Plan

* Milestone 1: MR1 (03.04.2025) 
  * At Milestone 1, the project proposal as well as the project contract will be finished and handed in. Furthermore, the first sketch of the application architecture will be finished and discussed. 
* Milestone 2: MR2 (ca. 21.05.2025) 
  * At Milestone 2, the first data pipeline will be functional. Basic user management will be implemented. Additionally, the Micro-History Lens feature will be functional. 
* Milestone 3: MR3 (18.06.2025) 
  * At Milestone 3, all five main features will be functional. 

# Iceberglist

<table>
<tr>
<td>

**No.**
</td>
<td>

**Group**
</td>
<td>

**Feature**
</td>
<td>

**Actor**
</td>
<td>

**Short Description**
</td>
<td>

**Priority**
</td>
<td>

**Effort (h)**
</td>
<td>

**Responsible**
</td>
</tr>
<tr>
<td>1.1</td>
<td>Registration & Identity</td>
<td>Email Registration </td>
<td>Tourist </td>
<td>Register with email </td>
<td>H</td>
<td>10</td>
<td>Bianca</td>
</tr>
<tr>
<td>1.2</td>
<td>Registration & Identity</td>
<td>Login & Session Handling </td>
<td>Tourist </td>
<td>Login and stay authenticated</td>
<td>H</td>
<td>10</td>
<td>Bianca</td>
</tr>
<tr>
<td>1.3</td>
<td>Registration & Identity</td>
<td>Password Recovery</td>
<td>Tourist</td>
<td>Reset forgotten password</td>
<td>M</td>
<td>10</td>
<td>Bianca</td>
</tr>
<tr>
<td>1.4</td>
<td>Registration & Identity</td>
<td>Account Deletion (GDPR)</td>
<td>Tourist</td>
<td>User can delete account</td>
<td>H</td>
<td>10</td>
<td>Bianca</td>
</tr>
<tr>
<td>1.5</td>
<td>Registration & Identity</td>
<td>Session Timeout </td>
<td>System </td>
<td>Auto-logout after inactivity </td>
<td>M</td>
<td>5</td>
<td>Bianca</td>
</tr>
<tr>
<td>2.1</td>
<td>Historical Content Engine</td>
<td>Wikidata Scraper </td>
<td>System </td>
<td>Import stories  </td>
<td>H</td>
<td>40</td>
<td>Valentin</td>
</tr>
<tr>
<td>2.2</td>
<td>Historical Content Engine</td>
<td>Tag Normalizer </td>
<td>System </td>
<td>Normalize categories (e.g. art, politics)</td>
<td>M</td>
<td>15</td>
<td>Simon</td>
</tr>
<tr>
<td>2.3</td>
<td>Historical Content Engine</td>
<td>DB Schema & Story Loader </td>
<td>System </td>
<td>Store and prepare stories </td>
<td>M</td>
<td>15</td>
<td>Simon</td>
</tr>
<tr>
<td>2.4</td>
<td>Historical Content Engine</td>
<td>Media Fetcher </td>
<td>System </td>
<td>Get thumbnails and media files </td>
<td>M</td>
<td>20</td>
<td>Simon</td>
</tr>
<tr>
<td>2.5</td>
<td>Historical Content Engine</td>
<td>API Story Delivery </td>
<td>System </td>
<td>Serve stories to frontend </td>
<td>H</td>
<td>20</td>
<td>Valentin</td>
</tr>
<tr>
<td>3.1</td>
<td>User Interest Profile </td>
<td>Onboarding UI </td>
<td>Tourist </td>
<td>Select themes at app start</td>
<td>H</td>
<td>10</td>
<td>Hariton</td>
</tr>
<tr>
<td>3.2</td>
<td>User Interest Profile </td>
<td>Interest Editor </td>
<td>Tourist </td>
<td>Edit interests anytime </td>
<td>M</td>
<td>10</td>
<td>Ivan</td>
</tr>
<tr>
<td>3.3</td>
<td>User Interest Profile </td>
<td>Interest DB Model </td>
<td>System </td>
<td>Store weights per category </td>
<td>H</td>
<td>10</td>
<td>Ivan</td>
</tr>
<tr>
<td>3.4</td>
<td>User Interest Profile </td>
<td>Highlight Matching Reason </td>
<td>Tourist </td>
<td>Explain why story matches </td>
<td>M</td>
<td>10</td>
<td>Ivan</td>
</tr>
<tr>
<td>4.1</td>
<td>Tour Planning </td>
<td>Tour Creation UI </td>
<td>Tourist </td>
<td>Choose POIs on map </td>
<td>H</td>
<td>35</td>
<td>Christoph</td>
</tr>
<tr>
<td>4.2</td>
<td>Tour Planning </td>
<td>Tour Sort by Interest </td>
<td>System </td>
<td>Rank POIs in user order </td>
<td>H</td>
<td>15</td>
<td>Ivan</td>
</tr>
<tr>
<td>4.3</td>
<td>Tour Planning </td>
<td>Export to JSON </td>
<td>Tourist </td>
<td>Download tour for later </td>
<td>M</td>
<td>10</td>
<td>Hariton</td>
</tr>
<tr>
<td>4.4</td>
<td>Tour Planning </td>
<td>Import Saved Tour </td>
<td>Tourist </td>
<td>Resume previous tours </td>
<td>M</td>
<td>10</td>
<td>Hariton</td>
</tr>
<tr>
<td>4.5</td>
<td>Tour Planning </td>
<td>Tour Duration Estimation </td>
<td>Tourist </td>
<td>Estimate time needed </td>
<td>M</td>
<td>10</td>
<td>Ivan</td>
</tr>
<tr>
<td>4.6</td>
<td>Tour Planning </td>
<td>Visual Route Preview </td>
<td>Tourist </td>
<td>Map view of route </td>
<td>M</td>
<td>10</td>
<td>Christoph</td>
</tr>
<tr>
<td>5.1</td>
<td>Recommendation Engine </td>
<td>Interest Score Function </td>
<td>System </td>
<td>Score content for user </td>
<td>H</td>
<td>35</td>
<td>Ivan</td>
</tr>
<tr>
<td>5.2</td>
<td>Recommendation Engine </td>
<td>Freshness/Diversity Filter </td>
<td>System </td>
<td>Avoid repetition </td>
<td>H</td>
<td>15</td>
<td>Christoph</td>
</tr>
<tr>
<td>5.3</td>
<td>Recommendation Engine </td>
<td>Prioritized Suggestion Feed</td>
<td>Tourist </td>
<td>Get top matches </td>
<td>M</td>
<td>15</td>
<td>Christoph</td>
</tr>
<tr>
<td>5.4</td>
<td>Recommendation Engine </td>
<td>Trending Story Boost </td>
<td>System </td>
<td>Promote relevant stories </td>
<td>M</td>
<td>15</td>
<td>Hariton</td>
</tr>
<tr>
<td>5.5</td>
<td>Recommendation Engine </td>
<td>Interest-Topic Hybrid </td>
<td>System </td>
<td>Combine topics & behavior </td>
<td>H</td>
<td>15</td>
<td>Ivan</td>
</tr>
<tr>
<td>5.6</td>
<td>Recommendation Engine </td>
<td>Fallback Suggestion Flow </td>
<td>Tourist </td>
<td>What if no good match? </td>
<td>M</td>
<td>15</td>
<td>Simon</td>
</tr>
<tr>
<td>6.1</td>
<td>Search History </td>
<td>Track Story Clicks </td>
<td>System </td>
<td>Monitor engagement </td>
<td>H</td>
<td>30</td>
<td>Simon</td>
</tr>
<tr>
<td>6.2</td>
<td>Search History </td>
<td>Extract User Keywords </td>
<td>System </td>
<td>Parse search terms </td>
<td>H</td>
<td>10</td>
<td>Simon</td>
</tr>
<tr>
<td>6.3</td>
<td>Search History </td>
<td>Adapt Interest Weights </td>
<td>System </td>
<td>Update user profile </td>
<td>M</td>
<td>10</td>
<td>Simon</td>
</tr>
<tr>
<td>6.4</td>
<td>Search History </td>
<td>Dwell Time Monitoring </td>
<td>System </td>
<td>Gauge story quality </td>
<td>M</td>
<td>10</td>
<td>Simon</td>
</tr>
<tr>
<td>6.5</td>
<td>Search History </td>
<td>Time-Based Decay Model </td>
<td>System </td>
<td>Reduce outdated tags </td>
<td>M</td>
<td>10</td>
<td>Simon</td>
</tr>
<tr>
<td>7.1</td>
<td>Gamification </td>
<td>XP Tracker </td>
<td>Tourist </td>
<td>Earn points for reading </td>
<td>H</td>
<td>30</td>
<td>Bianca</td>
</tr>
<tr>
<td>7.2</td>
<td>Gamification </td>
<td>Visit Badge Unlocks </td>
<td>Tourist </td>
<td>Badge for visiting locations </td>
<td>M</td>
<td>10</td>
<td>Ivan</td>
</tr>
<tr>
<td>7.3</td>
<td>Gamification </td>
<td>Leaderboard Ranking </td>
<td>Tourist </td>
<td>Compare with others, friends </td>
<td>M</td>
<td>10</td>
<td>Ivan</td>
</tr>
<tr>
<td>7.4</td>
<td>Gamification </td>
<td>Trip Goals </td>
<td>Tourist </td>
<td>Small challenges </td>
<td>M</td>
<td>10</td>
<td>Simon</td>
</tr>
<tr>
<td>7.5</td>
<td>Gamification </td>
<td>Tour Completion Bonus (10 Question) </td>
<td>Tourist </td>
<td>Extra points at end </td>
<td>M</td>
<td>10</td>
<td>Hariton</td>
</tr>
<tr>
<td>7.6</td>
<td>Gamification </td>
<td>Riddle, Question Games and Quests</td>
<td>System </td>
<td>Keep user engaged  </td>
<td>H</td>
<td>25</td>
<td>Christoph</td>
</tr>
<tr>
<td>7.7</td>
<td>Gamification </td>
<td>Compete with Friends</td>
<td>System </td>
<td>Compete with People, which are on the same Trip </td>
<td>H</td>
<td>25</td>
<td>Valentin</td>
</tr>
<tr>
<td>8.1</td>
<td>Budget Tours </td>
<td>Cost Labeling of POIs </td>
<td>System </td>
<td>Assign price estimates </td>
<td>H</td>
<td>10</td>
<td>Christoph</td>
</tr>
<tr>
<td>8.2</td>
<td>Budget Tours </td>
<td>Free Tour Filter </td>
<td>Tourist </td>
<td>Only show free stops </td>
<td>M</td>
<td>10</td>
<td>Christoph</td>
</tr>
<tr>
<td>8.3</td>
<td>Budget Tours </td>
<td>Tour Budget Planner </td>
<td>Tourist </td>
<td>Input max amount of time and money available </td>
<td>M</td>
<td>10</td>
<td>Ivan</td>
</tr>
<tr>
<td>8.4</td>
<td>Budget Tours </td>
<td>Time + Cost Optimization </td>
<td>System </td>
<td>Solve most interesting tour </td>
<td>M</td>
<td>10</td>
<td>Hariton</td>
</tr>
<tr>
<td>8.5</td>
<td>Budget Tours </td>
<td>Budget Export Summary </td>
<td>Tourist </td>
<td>Total time & cost </td>
<td>M</td>
<td>10</td>
<td>Hariton</td>
</tr>
<tr>
<td>8.6</td>
<td>Budget Tours </td>
<td>Crowdsourced Cost Updates </td>
<td>Tourist </td>
<td>Suggest updates of prices </td>
<td>M</td>
<td>10</td>
<td>Christoph</td>
</tr>
<tr>
<td>9.1</td>
<td>Fun Facts </td>
<td>Extract Key Sentence </td>
<td>System </td>
<td>Highlight key info </td>
<td>M</td>
<td>10</td>
<td>Bianca</td>
</tr>
<tr>
<td>9.2</td>
<td>Fun Facts </td>
<td>Image Story Pairing </td>
<td>System </td>
<td>Add media to card </td>
<td>M</td>
<td>10</td>
<td>Bianca</td>
</tr>
<tr>
<td>9.3</td>
<td>Fun Facts </td>
<td>Share Card Preview </td>
<td>Tourist </td>
<td>See card before sharing </td>
<td>M</td>
<td>10</td>
<td>Bianca</td>
</tr>
<tr>
<td>9.4</td>
<td>Fun Facts </td>
<td>Social Share Button </td>
<td>Tourist </td>
<td>Send via WhatsApp, etc. </td>
<td>M</td>
<td>10</td>
<td>Bianca</td>
</tr>
<tr>
<td>9.5</td>
<td>Fun Facts </td>
<td>Mini Fact Collection </td>
<td>Tourist </td>
<td>Save your favorites </td>
<td>M</td>
<td>10</td>
<td>Bianca</td>
</tr>
<tr>
<td>10.1</td>
<td>Smart Alerts </td>
<td>Geofence Logic </td>
<td>System </td>
<td>Detect user near POI </td>
<td>H</td>
<td>15</td>
<td>Hariton</td>
</tr>
<tr>
<td>10.2</td>
<td>Smart Alerts </td>
<td>Match by Interest</td>
<td>System </td>
<td>Filter to user tags </td>
<td>M</td>
<td>10</td>
<td>Hariton</td>
</tr>
<tr>
<td>10.3</td>
<td>Smart Alerts </td>
<td>Notification Builder </td>
<td>System </td>
<td>Create popup </td>
<td>M</td>
<td>10</td>
<td>Hariton</td>
</tr>
<tr>
<td>10.4</td>
<td>Smart Alerts </td>
<td>Quiet Mode Setting </td>
<td>Tourist </td>
<td>Turn off the alerts for a limited time or indefinitely </td>
<td>M</td>
<td>10</td>
<td>Hariton</td>
</tr>
<tr>
<td>10.5</td>
<td>Smart Alerts </td>
<td>Snooze or Remind Later </td>
<td>Tourist </td>
<td>Allows stories to be postponed and resurfaced </td>
<td>M</td>
<td>10</td>
<td>Hariton</td>
</tr>
<tr>
<td>10.6</td>
<td>Smart Alerts </td>
<td>Popup with Action Link </td>
<td>Tourist </td>
<td>Tap to read full </td>
<td>M</td>
<td>15</td>
<td>Bianca</td>
</tr>
<tr>
<td>11.1</td>
<td>Offline </td>
<td>Offline Story Cache </td>
<td>Tourist </td>
<td>Save story for offline read </td>
<td>H</td>
<td>35</td>
<td>Valentin</td>
</tr>
<tr>
<td>11.2</td>
<td>Offline </td>
<td>Offline Map Tile Support </td>
<td>Tourist </td>
<td>Access city maps without internet </td>
<td>H</td>
<td>15</td>
<td>Valentin</td>
</tr>
<tr>
<td>12.1</td>
<td>Intro </td>
<td>Intro Tour & Tutorial </td>
<td>Tourist </td>
<td>Walkthrough at first start </td>
<td>M</td>
<td>10</td>
<td>Hariton</td>
</tr>
</table>

