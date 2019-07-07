clear memo
set cent on
set date to german

tstr_1 = 'A'
tstr_eng = 'Hello mem'
tstr_cp866 = 'Привет мем'

tnum_0 = 0
tnum_1 = 1
tnum_1neg = -1
tnum_pi = 3.14159265
tnum_rur = 201401.18
tnum_neg = -919.19

ttrue  = .T.
tfalse = .F.

tdt_140120 = ctod('20.01.2014')
tdt_700101 = ctod('01.01.1970')

dimension tarr_one (4)

tarr_one(1) = 'A1'
tarr_one(2) = 'A2'
tarr_one(3) = 'A3'
tarr_one(4) = 'A4'

dimension tarr_two (3,2)

tarr_two(1,1) = 'A 1x1'
tarr_two(1,2) = 'A 1x2'
tarr_two(2,1) = 21
tarr_two(2,2) = .T.
tarr_two(3,1) = 31
tarr_two(3,2) = 'A 3x2'

save to test_mem
