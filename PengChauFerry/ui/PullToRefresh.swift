// https://github.com/siteline/SwiftUIRefresh/blob/master/Sources/PullToRefresh.swift
import SwiftUI
//import Introspect
import UIKit

#if os(macOS)
public typealias PlatformView = NSView
#endif
#if os(iOS) || os(tvOS)
public typealias PlatformView = UIView
#endif

private struct PullToRefresh: UIViewRepresentable {

    @Binding var isShowing: Bool
    let onRefresh: () -> Void

    public init(
        isShowing: Binding<Bool>,
        onRefresh: @escaping () -> Void
    ) {
        _isShowing = isShowing
        self.onRefresh = onRefresh
    }

    public class Coordinator {
        let onRefresh: () -> Void
        let isShowing: Binding<Bool>

        init(
            onRefresh: @escaping () -> Void,
            isShowing: Binding<Bool>
        ) {
            self.onRefresh = onRefresh
            self.isShowing = isShowing
        }

        @objc
        func onValueChanged() {
            isShowing.wrappedValue = true
            onRefresh()
        }
    }

    public func makeUIView(context: UIViewRepresentableContext<PullToRefresh>) -> UIView {
        let view = UIView(frame: .zero)
        view.isHidden = true
        view.isUserInteractionEnabled = false
        return view
    }

    /// Finds an ancestor of the specified type.
    /// If it reaches the top of the view without finding the specified view type, it returns nil.
    static func findAncestor<AnyViewType: PlatformView>(ofType type: AnyViewType.Type, from entry: PlatformView) -> AnyViewType? {
        var superview = entry.superview
        while let s = superview {
            if let typed = s as? AnyViewType {
                return typed
            }
            superview = s.superview
        }
        return nil
    }

    /// Finds the view host of a specific view.
    /// SwiftUI wraps each UIView within a ViewHost, then within a HostingView.
    /// Returns nil if it couldn't find a view host. This should never happen when called with an IntrospectionView.
    static func findViewHost(from entry: PlatformView) -> PlatformView? {
        var superview = entry.superview
        while let s = superview {
            if NSStringFromClass(type(of: s)).contains("ViewHost") {
                return s
            }
            superview = s.superview
        }
        return nil
    }

    /// Finds a subview of the specified type.
    /// This method will recursively look for this view.
    /// Returns nil if it can't find a view of the specified type.
    static func findChild<AnyViewType: PlatformView>(
        ofType type: AnyViewType.Type,
        in root: PlatformView
    ) -> AnyViewType? {
        for subview in root.subviews {
            if let typed = subview as? AnyViewType {
                return typed
            } else if let typed = findChild(ofType: type, in: subview) {
                return typed
            }
        }
        return nil
    }

    /// Finds a previous sibling that contains a view of the specified type.
    /// This method inspects siblings recursively.
    /// Returns nil if no sibling contains the specified type.
    static func previousSibling<AnyViewType: PlatformView>(
        containing type: AnyViewType.Type,
        from entry: PlatformView
    ) -> AnyViewType? {

        guard let superview = entry.superview,
            let entryIndex = superview.subviews.firstIndex(of: entry),
            entryIndex > 0
            else {
                return nil
        }

        for subview in superview.subviews[0..<entryIndex].reversed() {
            if let typed = findChild(ofType: type, in: subview) {
                return typed
            }
        }

        return nil
    }

    private func findUITableView(entry: UIView) -> UITableView? {
        // Search in ancestors
//        if let tableView = PullToRefresh.findAncestor(ofType: UITableView.self, from: entry) {
//            return tableView
//        }

        guard let viewHost = PullToRefresh.findViewHost(from: entry) else {
            return nil
        }

        // Search in siblings
        return PullToRefresh.previousSibling(containing: UITableView.self, from: viewHost)
    }

    public func updateUIView(_ uiView: UIView, context: UIViewRepresentableContext<PullToRefresh>) {

        DispatchQueue.main.asyncAfter(deadline: .now()) {

            guard let tableView = self.findUITableView(entry: uiView) else {
                return
            }

            if let refreshControl = tableView.refreshControl {
                if self.isShowing {
                    refreshControl.beginRefreshing()
                } else {
                    refreshControl.endRefreshing()
                }
                return
            }

            let refreshControl = UIRefreshControl()
            refreshControl.addTarget(context.coordinator, action: #selector(Coordinator.onValueChanged), for: .valueChanged)
            tableView.refreshControl = refreshControl
        }
    }

    public func makeCoordinator() -> Coordinator {
        return Coordinator(onRefresh: onRefresh, isShowing: $isShowing)
    }
}

extension List {
    public func pullToRefresh(isShowing: Binding<Bool>, onRefresh: @escaping () -> Void) -> some View {
        return overlay(
            PullToRefresh(isShowing: isShowing, onRefresh: onRefresh)
                .frame(width: 0, height: 0)
        )
    }
}
