//
//  SkyFloatingLabelTextField+Binding.swift
//
//  Created by Volodymyr Chernyshov on 06.04.2020.
//  Copyright © 2020 Garage Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary
import SkyFloatingLabelTextField

public extension SkyFloatingLabelTextField: TextInputLayout {
    public func getTextField() -> UITextField {
        return self
    }

    public func setError(error: String?) {
        self.errorMessage = error
    }
}