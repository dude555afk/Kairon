import 'package:flutter_test/flutter_test.dart';
import 'package:kelivo/core/providers/settings_provider.dart';

void main() {
  test('Kilo built-in defaults to anonymous Auto Free', () {
    final config = ProviderConfig.defaultsFor('Kilo');

    expect(config.enabled, isTrue);
    expect(config.baseUrl, 'https://api.kilo.ai/api/gateway');
    expect(config.apiKey, isEmpty);
    expect(config.chatPath, '/chat/completions');
    expect(config.useResponseApi, isFalse);
    expect(config.models, contains('kilo-auto/free'));
  });
}
