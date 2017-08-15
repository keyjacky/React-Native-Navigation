Pod::Spec.new do |s|
s.name = 'react-native-navigation'
s.version = '1.1.x'
s.license = 'MIT'
s.summary = 'A complete native navigation solution for React Native'
s.homepage = 'https://github.com/keyjacky/React-Native-Navigation'
s.authors = { 'Kopus' => '421183082@qq.com' }
s.source = { :git => 'https://github.com/keyjacky/React-Native-Navigation.git', :tag => s.version.to_s }
s.requires_arc = true
s.ios.deployment_target = '8.0'
s.source_files = 'ios/**/*.{h,m}',
end