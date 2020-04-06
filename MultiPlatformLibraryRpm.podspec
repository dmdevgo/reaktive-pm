Pod::Spec.new do |spec|
    spec.name                     = 'MultiPlatformLibraryRpm'
    spec.version                  = '0.0.1'
    spec.homepage                 = 'Link to a Kotlin/Native module homepage'
    spec.source                   = { :git => "Not Published", :tag => "Cocoapods/#{spec.name}/#{spec.version}" }
    spec.authors                  = 'Garage Development'
    spec.license                  = { :type => 'MIT', :file => 'LICENSE' }
    spec.summary                  = 'Swift additions to reaktive-pm Kotlin/Native library'
    spec.module_name              = "#{spec.name}"
    
    spec.dependency 'MultiPlatformLibrary'

    spec.ios.deployment_target  = '9.0'
    spec.swift_version = '4.2'

    spec.subspec 'base' do |sp|
      sp.source_files = "rpm/src/iosMain/swift/base/**/*.{h,m,swift}"
    end

    #spec.subspec 'AlamofireImage' do |sp|
    #  sp.source_files = "mvvm/src/iosMain/swift/AlamofireImage/**/*.{h,m,swift}"
    #  sp.dependency 'AlamofireImage'
    #end

    #spec.subspec 'SkyFloatingLabelTextField' do |sp|
    #  sp.source_files = "mvvm/src/iosMain/swift/SkyFloatingLabelTextField/**/*.{h,m,swift}"
    #  sp.dependency 'SkyFloatingLabelTextField'
    #end

    spec.pod_target_xcconfig = {
        'VALID_ARCHS' => '$(ARCHS_STANDARD_64_BIT)'
    }
end
