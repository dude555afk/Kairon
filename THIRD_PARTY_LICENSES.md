# Third-Party Licenses

This project includes the following third-party binaries in `androidApp/src/main/jniLibs/`:

## PRoot (Termux fork)

- **Files:** `libproot.so`, `libproot-loader.so`, `libproot-loader32.so`
- **Source:** https://github.com/termux/proot
- **License:** GPL-2.0
- **Copyright:** Copyright (C) PRoot developers

PRoot is a user-space implementation of chroot, mount --bind, and binfmt_misc. It is used to run an Alpine Linux environment inside the Android app without requiring root access. PRoot is executed as a separate process and is not linked into the application code.

The full GPL-2.0 license text is available at: https://www.gnu.org/licenses/old-licenses/gpl-2.0.html

## talloc

- **Files:** `libtalloc.so`
- **Source:** https://talloc.samba.org/
- **License:** LGPL-3.0
- **Copyright:** Copyright (C) Andrew Tridgell, Stefan Metzmacher, and contributors

talloc is a hierarchical memory allocator used as a dependency of PRoot. It is dynamically linked.

The full LGPL-3.0 license text is available at: https://www.gnu.org/licenses/lgpl-3.0.html


## LastChat UI source

- Source: https://github.com/Cocolalilal/LastChat
- Upstream branch: `LastChat`
- License: GNU Affero General Public License v3.0 (AGPL-3.0)
- Adapted UI files: design tokens, composer shell, grouped message bubble, conversation row surface, modal drawer sheet, drawer action and menu button.
- Package paths were adapted for Kairon. Existing providers, chat state, persistence, tools, permissions, attachment handling, Markdown parsing, speech and callbacks remain Kairon implementations.
- Adapted files contain attribution and modification notices.

The previous Apache-2.0 license text for applicable pre-existing Kairon components is retained in `LICENSE-APACHE-2.0.txt`. The combined Kairon distribution is offered under AGPL-3.0; individual third-party components retain their applicable notices and licenses.
