import SwiftUI

@main
struct iOSApp: App {

    init(){
        Main_iosKT.doInitKoin()
    }

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}