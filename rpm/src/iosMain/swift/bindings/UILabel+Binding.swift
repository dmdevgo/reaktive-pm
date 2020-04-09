//
//  UILabel+Binding.swift
//
//  Created by Volodymyr Chernyshov on 06.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary

public extension UILabel {
    public func text() -> ConsumerWrapper<NSString> {
        return BindingsKt.text(self)
    }
}