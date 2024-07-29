package com.example.assignment5.core.util

import android.content.ContentValues.TAG
import android.util.Log
import com.example.assignment5.core.di.Dispatcher
import com.example.assignment5.core.di.MyDispatchers
import com.example.assignment5.data.entity.toSingerWithSongs
import com.example.assignment5.data.model.Singer
import com.example.assignment5.data.model.Song
import com.example.assignment5.data.repository.SingerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MockData @Inject constructor(
    private val singerRepository: SingerRepository,
    @Dispatcher(MyDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {
    private val singers = listOf(
        Singer(
            id = 0,
            name = "Đen",
            songs = listOf(
                Song(
                    id = 0,
                    title = "Nấu Ăn Cho Em",
                    albumCover = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/images%2F37_den-vau-top-2-4946-1686734959.jpg?alt=media&token=691f2510-c6ce-45fc-9f4e-1752dfeb72c9",
                    url = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/mp3%2F37-%20N%E1%BA%A5u%20%C4%83n%20cho%20em.mp3?alt=media&token=fc700920-bb45-4c6b-b818-a534efa4e596"
                )
            )
        ),
        Singer(
            id = 0,
            name = "Vũ Cát Tường",
            songs = listOf(
                Song(
                    id = 0,
                    title = "Vết Mưa",
                    albumCover = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/images%2F38%20ab67616d00001e024992bf290fe8c9f82a0e66e4.jpg?alt=media&token=a9e3e9de-bca1-4ca7-9a3f-0216f1c669e1",
                    url = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/mp3%2F38%20-%20V%E1%BA%BFt%20M%C6%B0a.mp3?alt=media&token=81b24527-a67e-43d7-884d-9d078b35c49c"
                ),
                Song(
                    id = 0,
                    title = "Từng Là",
                    albumCover = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/images%2F40_ab67616d0000b27373fd9df745f312dbbe04bf49.jpg?alt=media&token=6f8e7a32-260a-4c64-8d8e-72664ba31118",
                    url = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/mp3%2F40%20-%20T%E1%BB%ABng%20L%C3%A0.mp3?alt=media&token=c3693654-55b3-4ae7-b802-469aedd3ad33"
                )
            )
        ),
        Singer(
            id = 0,
            name = "Da Lab",
            songs = listOf(
                Song(
                    id = 0,
                    title = "Thanh Xuân",
                    albumCover = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/images%2F39_1535121377317_640.jpg?alt=media&token=9e3b90ec-2cbb-4f92-840f-a5412014873d",
                    url = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/mp3%2F39%20-%20Thanh%20Xu%C3%A2n.mp3?alt=media&token=7aeaa707-3ff9-4d33-9f43-532bcdc49fb2"
                )
            )
        ),
        Singer(
            id = 0,
            name = "Westlife",
            songs = listOf(
                Song(
                    id = 0,
                    title = "My Love",
                    albumCover = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/images%2F41_ab67616d0000b2734ba8ccd7276fab931c9692b5.jpg?alt=media&token=90c86144-afc3-43fe-a91f-eeadc12ee5c1",
                    url = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/mp3%2F41%20-%20My%20Love.mp3?alt=media&token=29cccc12-ded2-4e23-8433-962e71e425ad"
                ),
                Song(
                    id = 0,
                    title = "I Lay My Love On You",
                    albumCover = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/images%2F42_ab67616d0000b273e05c645c451f0a57b8b5474f.jpg?alt=media&token=374de400-1bec-42cd-9b04-78a62b14d7c6",
                    url = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/mp3%2F42%20-%20I%20Lay%20My%20Love%20on%20You.mp3?alt=media&token=f59d884b-485d-4ee4-96f0-1accb2bd1983"
                ),
                Song(
                    id = 0,
                    title = "You Raise Me Up",
                    albumCover = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/images%2F43_man_lifting_child.jpg?alt=media&token=98c7edd9-2bda-4579-8056-660198cd92de",
                    url = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/mp3%2F43%20-%20You%20Raise%20Me%20Up.mp3?alt=media&token=67b0a1c1-8e5b-4eab-84b6-7ac48f18e25f"
                )
            )
        ),
        Singer(
            id = 0,
            name = "Shane Filan",
            songs = listOf(
                Song(
                    id = 0,
                    title = "Beautiful In White",
                    albumCover = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/images%2F44_ab67616d0000b273650a02d7b740a70fc7d1806e.jpg?alt=media&token=892ba06a-e722-4ddb-ad11-ded2baedb4c1",
                    url = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/mp3%2F44%20-%20Beautiful%20In%20White.mp3?alt=media&token=35c57217-4806-4ae2-bc36-d1107fe696ec"
                )
            )
        ),
        Singer(
            id = 0,
            name = "Bruno Mars",
            songs = listOf(
                Song(
                    id = 0,
                    title = "When I Was Your Man",
                    albumCover = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/images%2F45_Bruno_Mars_When_I_Was_Your_Man_Music_Video-993663645-large.jpg?alt=media&token=682c2659-35ff-4d76-992c-0ec826c486f2",
                    url = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/mp3%2F45%20-%20When%20I%20Was%20Your%20Man.mp3?alt=media&token=0646dd71-51e9-4c55-8bcc-7044f41d8874"
                ),
                Song(
                    id = 0,
                    title = "Just The Way You Are",
                    albumCover = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/images%2F48_ab67616d0000b273f60070dce96a2c1b70cf6ff0.jpg?alt=media&token=d4837655-e4c8-4da4-911a-15dcc0a10a46",
                    url = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/mp3%2F48%20-%20Just%20the%20Way%20You%20Are.mp3?alt=media&token=1864cf57-69f2-4345-ad1a-db1d048b9e05"
                )
            )
        ),
        Singer(
            id = 0,
            name = "The Beatles",
            songs = listOf(
                Song(
                    id = 0,
                    title = "Here Come The Sun",
                    albumCover = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/images%2F46_ab67616d0000b273dc30583ba717007b00cceb25.jpg?alt=media&token=4366c7ef-737a-4166-8efc-9fb2c3b8d412",
                    url = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/mp3%2F46%20-%20Here%20Comes%20The%20Sun%20-%20Remastered%202009.mp3?alt=media&token=b344f9e1-1a5f-42fd-8d12-b7748e4d914e"
                ),
                Song(
                    id = 0,
                    title = "Yesterday",
                    albumCover = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/images%2F47_The-Beatles-Yesterday-EP-cover-820-1024x1024.jpg?alt=media&token=886d025f-e5be-4c65-8cc0-75eedf9cef39",
                    url = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/mp3%2F47%20-%20Yesterday%20-%20Remastered%202009.mp3?alt=media&token=4f49e65b-15c3-4771-ac1f-c9273549e0b1"
                ),
                Song(
                    id = 0,
                    title = "Let It Be",
                    albumCover = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/images%2F49_ab67616d0000b27384243a01af3c77b56fe01ab1.jpg?alt=media&token=40c92be3-3f64-4cf1-b3d5-3c8815e5d145",
                    url = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/mp3%2F49%20-%20Let%20It%20Be%20-%20Remastered%202009.mp3?alt=media&token=f73f95a8-3cfe-4d54-8e3d-e3cc43370f61"
                ),
                Song(
                    id = 0,
                    title = "Hey Jude",
                    albumCover = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/images%2F50_MV5BZDRiNjliMzYtMzJjNi00ZmQ3LWIyYTAtMGVkMDE4ODVjYjI4XkEyXkFqcGdeQXVyMjUyNDk2ODc%40._V1_.jpg?alt=media&token=6242da13-6963-4665-a7af-a3ea17981a89",
                    url = "https://firebasestorage.googleapis.com/v0/b/music-data-ae293.appspot.com/o/mp3%2F50%20-%20Hey%20Jude%20-%202015%20Mix.mp3?alt=media&token=4281e804-7ac1-43e0-958c-8942977b1ba1"
                )
            )
        )
    )

    private suspend fun insertMockData() {
        withContext(ioDispatcher) {
            Log.d(TAG, "insertMockData: Inserting mock data")
            singers.forEach { singer ->
                singerRepository.insertSinger(singer.toSingerWithSongs())
            }
        }
    }

    suspend operator fun invoke() {
        insertMockData()
    }
}